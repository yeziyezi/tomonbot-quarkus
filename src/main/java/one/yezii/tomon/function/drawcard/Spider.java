package one.yezii.tomon.function.drawcard;

import one.yezii.tomon.push.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Spider {
    private final static String baseUrl = "http://prts.wiki";
    private final static DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm", Locale.CHINA);
    private final static Logger logger = LoggerFactory.getLogger(Spider.class);

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        List<Pool> pools = new Spider().fetchPools();
    }

    public List<Pool> fetchPools() {
        Optional<String[]> tbodyArr = fetchTBody();
        if (tbodyArr.isEmpty()) {
            logger.warn("抓取卡池内容失败");
            return Collections.emptyList();
        }
        try {
            String limitTBody = tbodyArr.get()[0];
            String commonTbody = tbodyArr.get()[1];
            List<String> commonRows = getRowsFromTbody(commonTbody);
            List<String> limitRows = getRowsFromTbody(limitTBody);
            List<Pool> limitPools = limitRows.stream()
                    .map(row -> getPoolFromTableRow(row, true))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            List<Pool> commonPools = commonRows.stream()
                    .map(row -> getPoolFromTableRow(row, false))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            limitPools.addAll(commonPools);
            return limitPools;
        } catch (Exception e) {
            logger.warn("抓取卡池内容失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 从tbody中解析出tr
     *
     * @param tbody table body
     * @return table rows
     */
    private List<String> getRowsFromTbody(String tbody) {
        return getGroupMatches(tbody, "<tr>[\\S\\s]+?</tr>", 0);
    }

    /**
     * 抓取卡池tbody
     *
     * @return String[0]限时池，String[1]常驻轮换池
     */
    private Optional<String[]> fetchTBody() {
        String url = baseUrl + "/w/%E5%8D%A1%E6%B1%A0%E4%B8%80%E8%A7%88";
        String html;
        try {
            html = HttpUtil.doGet(url).body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            logger.warn("获取页面内容失败,url={}", url);
            return Optional.empty();
        }
        Matcher matcher = Pattern.compile("<tbody>[\\s\\S]+?</tbody>").matcher(html);
        List<String> tbodyList = new ArrayList<>();
        while (matcher.find()) {
            tbodyList.add(matcher.group()
                    .replaceAll("<img[^<>]+>", "")
                    .replaceAll("<br />", "")
                    .replaceAll("<span[^<>]*>[^<>]*</span>", "")
                    .replaceAll("<span[^<>]*>", "")
                    .replaceAll("</span>", "")
                    .replaceAll("\\s<th[\\s\\S]+?</th>", "")
                    .replaceAll("<tr></tr>", "")
                    .replaceAll("\n", ""));
        }
        String limitTbody = tbodyList.get(0);
        String commonTbody = tbodyList.get(1);
        return Optional.of(new String[]{limitTbody, commonTbody});
    }

    private Pool getPoolFromTableRow(String tableRow, boolean isLimit) {
        //初始化一个基础概率正常的池子
        Pool pool = Pools.commonPool();

        //tdMatches中每一个子list为每个td的groups，group[0]为完整td匹配，group[1]...为匹配子串
        List<String> tdMatches = getGroupMatches(tableRow, "<td>([\\s\\S]+?)</td>", 1);
        //如果不是限时寻访，第一个td是序号，移除掉
        if (!isLimit) {
            tdMatches.remove(0);
        }
        //如果第一个td里没有跳转链接则无法取得池子里的干员列表，跳过
        if (!tdMatches.get(0).contains("<a href=\"/w")) {
            return null;
        }

        //获取卡池开放时间字符串
        pool.setOpenTime(tdMatches.get(1));

        //取得卡池抽取时间的截止日期，如果已经结束则跳过
        String poolCloseTime = tdMatches.get(1).split("~")[1].trim();
        boolean poolClosed = LocalDateTime.parse(poolCloseTime, dateTimeFormatter)
                .isBefore(LocalDateTime.now());
        if (poolClosed) {
            return null;
        }
        //解析出池子的url和名称
        List<String> poolUrlAndName = getMatches(tdMatches.get(0),
                "<a\\shref=\"(/w/[^\"]+)\"\\stitle=\"寻访模拟/([^\"]+)\">").get(0);
        pool.setName(poolUrlAndName.get(2));
        //解析出四五六星up对应的描述文本
        String sixStarUpText = tdMatches.get(2);
        String fiveAndFourStarUpText = tdMatches.get(3);
        //抓取卡池内干员列表
        List<Operator> operators = getOperatorsInPool(baseUrl + poolUrlAndName.get(1));
        //取得每个星级下的up干员名称列表
        Map<Integer, List<String>> upOperatorNamesOfStar = getUpOperatorNamesOfStar(operators, sixStarUpText,
                fiveAndFourStarUpText);
        //取得卡池的类型
        PoolDescriptor descriptor = getPoolDescriptor(isLimit,
                sixStarUpText, fiveAndFourStarUpText, upOperatorNamesOfStar.get(6).size(),
                upOperatorNamesOfStar.get(5).size());
        pool.setPoolDescriptor(descriptor);
        //根据卡池的类型给干员列表赋上同星级内的up概率
        setUpRateIntoOperators(operators, descriptor, upOperatorNamesOfStar.get(6),
                upOperatorNamesOfStar.get(5),
                upOperatorNamesOfStar.get(4));
        operators.forEach(pool::addOperator);
        return pool;
    }

    /**
     * 获取各星级up干员的名字
     *
     * @param operators             池子里全部干员列表
     * @param sixStarUpText         六星up字符串
     * @param fiveAndFourStarUpText 四五星up字符串
     * @return key星级，value up干员列表
     */
    private Map<Integer, List<String>> getUpOperatorNamesOfStar(List<Operator> operators, String sixStarUpText,
                                                                String fiveAndFourStarUpText) {
        String operatorNamePattern = "<a href=\"[^\"]+\"\\stitle=\"([^\"]+)\"";
        List<String> sixStarUpOperatorNames = getGroupMatches(sixStarUpText, operatorNamePattern, 1);
        List<String> fiveAndFourStarUpOperatorNames = getGroupMatches(fiveAndFourStarUpText, operatorNamePattern, 1);
        Map<Integer, List<String>> fourAndFiveStarUoOperatorNames =
                fiveAndFourStarUpOperatorNames.stream().collect(Collectors.groupingBy(name ->
                        operators.stream().filter(operator ->
                                operator.getName().equals(name)).findFirst().orElseThrow().getStar()));
        List<String> fiveStarUpOperatorNames = fourAndFiveStarUoOperatorNames.get(5);
        //常驻标准池没有4星up
        List<String> fourStarUpOperatorNames = fourAndFiveStarUoOperatorNames
                .getOrDefault(4, Collections.emptyList());
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(4, fourStarUpOperatorNames);
        map.put(5, fiveStarUpOperatorNames);
        map.put(6, sixStarUpOperatorNames);
        return map;
    }

    /**
     * 设置干员列表的upRate(同星级下的up后概率)
     *
     * @param operators               总干员列表
     * @param poolDescriptor          卡池描述符
     * @param sixStarUpOperatorNames  六星up干员名字列表
     * @param fiveStarUpOperatorNames 五星up干员名字列表
     * @param fourStarUpOperatorNames 四星up干员名字列表
     */
    private void setUpRateIntoOperators(List<Operator> operators, PoolDescriptor poolDescriptor,
                                        List<String> sixStarUpOperatorNames,
                                        List<String> fiveStarUpOperatorNames,
                                        List<String> fourStarUpOperatorNames) {
        for (Operator operator : operators) {
            if (operator.getStar() == 6
                    && sixStarUpOperatorNames.contains(operator.getName())) {
                //如果是六星限定池，70%概率UP干员均分，定向池100%，其余50%
                if (poolDescriptor.equals(PoolDescriptor.LIMIT_UP)) {
                    operator.setUpRate(0.7 / sixStarUpOperatorNames.size());
                } else if (poolDescriptor.equals(PoolDescriptor.FIVE_AND_SIX_BEAMED)) {
                    operator.setUpRate(1.0 / sixStarUpOperatorNames.size());
                } else {
                    operator.setUpRate(0.5 / sixStarUpOperatorNames.size());
                }
            }
            //五星up干员50%概率均分
            if (operator.getStar() == 5 && fiveStarUpOperatorNames.contains(operator.getName())) {
                if (poolDescriptor.equals(PoolDescriptor.FIVE_AND_SIX_BEAMED)) {
                    operator.setUpRate(1.0 / fiveStarUpOperatorNames.size());
                } else {
                    operator.setUpRate(0.5 / fiveStarUpOperatorNames.size());
                }
            }
            //四星up干员50%概率均分
            if (operator.getStar() == 4 && fourStarUpOperatorNames.contains(operator.getName())) {
                operator.setUpRate(0.5 / fourStarUpOperatorNames.size());
            }
        }
    }

    /**
     * 判断当前池子的类型，用于之后的概率计算
     *
     * @param isLimit         是否为限定池
     * @param sixStarUpText   六星up文本（td）
     * @param sixStarUpCount  六星up干员数
     * @param fiveStarUpCount 五星up干员数
     * @return 卡池描述符
     */
    private PoolDescriptor getPoolDescriptor(boolean isLimit, String sixStarUpText, String fiveAndFourStarUpText,
                                             int sixStarUpCount, int fiveStarUpCount) {
        if (sixStarUpText.contains("本寻访无出率提升") && fiveStarUpCount == 2) {
            //双五星池子
            return PoolDescriptor.ONLY_TWO_FIVE_UP;
        } else if (sixStarUpText.contains("寻访池内只有以上6")
                && fiveAndFourStarUpText.contains("寻访池内只有以上5")) {
            //五六星定向池
            return PoolDescriptor.FIVE_AND_SIX_BEAMED;
        } else if (sixStarUpText.contains("限定干员")) {
            //限定池，六星中up干员的出率在六星中占0.7
            return PoolDescriptor.LIMIT_UP;
        } else if (sixStarUpCount == 2) {
            return isLimit ? PoolDescriptor.TWO_SIX_UP : PoolDescriptor.COMMON_ROTATE;
            //普通的up池
        } else if (sixStarUpCount == 1) {
            return PoolDescriptor.SINGLE_SIX_UP;
        }
        return PoolDescriptor.OTHER;
    }

    private List<String> getGroupMatches(String origin, String pattern, int groupNum) {
        return getMatches(origin, pattern)
                .stream().map(list -> list.get(groupNum))
                .collect(Collectors.toList());
    }

    private List<List<String>> getMatches(String origin, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(origin);
        List<List<String>> allMatches = new ArrayList<>();
        while (matcher.find()) {
            List<String> matches = new ArrayList<>();
            int matchesCount = matcher.groupCount() + 1;
            for (int i = 0; i < matchesCount; i++) {
                matches.add(matcher.group(i));
            }
            allMatches.add(matches);
        }
        return allMatches;
    }

    private List<Operator> getOperatorsInPool(String url) {
        String html;
        try {
            html = HttpUtil.doGet(url).body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            logger.error("页面内容获取失败,url={}", url);
            return Collections.emptyList();
        }
        Pattern csvPattern = Pattern.compile("<script type=\"csv\" id=\"data_operator\">([^<>]+)</script>");
        Matcher matcher = csvPattern.matcher(html);
        if (!matcher.find()) {
            throw new RuntimeException("csv script tag not found");
        }
        String csv = matcher.group(1);
        return Arrays.stream(csv.split("\n"))
                .map(s -> s.split(","))
                .filter(arr -> arr.length == 5)
                .map(arr -> Operator.of(arr[1], Integer.parseInt(arr[2]) + 1, 0))
                .collect(Collectors.toList());
    }
}
