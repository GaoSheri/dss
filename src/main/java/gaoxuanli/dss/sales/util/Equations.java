package gaoxuanli.dss.sales.util;

import org.jfree.data.json.impl.JSONArray;
import org.jfree.data.json.impl.JSONObject;
import org.springframework.data.relational.core.sql.In;

import java.util.*;

public class Equations {
    List<List<Integer>> possibleVars;   // x1, x2, x3...
    List<Double> weight; // w1, w2, w3...
    Map<String, Double> limit; // x1<3, x2>=7...{"1lt":3.0, "2ge":7.0}
    List<Map<List<Double>, String>> conditions; // 15 x1 + 2.4 x2 + 12 x3 <= 100
    List<Double> goalValues;

    public Equations(List<Double> weight,
                     Map<String, Double> limit,
                     List<Map<List<Double>, String>> conditions) {
        this.weight = weight;
        this.limit = limit;
        this.conditions = conditions;
        possibleVars = new ArrayList<>();
        goalValues = new ArrayList<>();
    }

    public JSONObject getResult() {
        int best = solve();
        JSONObject json = new JSONObject();
        json.put("vars", possibleVars);
        json.put("goals", goalValues);
        json.put("best", best);
        return json;
    }

    public List<List<Integer>> getPossibleVars() {
        return possibleVars;
    }

    // particular solution
    public Integer solve() {
        // weight [192, 36, 12]
        // limit {"1ge": 5.0, "2ge": 3.0, "3le": 2.0}
        // conditions:
        //      condition: { [15.0, 2.4, 12.0, 100.0]: "le"}

        possibleVars.clear();
        goalValues.clear();

        Map<List<Double>, String> map = conditions.get(0);
        List<Double> params = map.keySet().iterator().next();
        double cst = params.get(params.size() - 1);
        String conditionSignal = map.get(params);

        System.out.println("------- Linear Programming -------");
        System.out.println("\n\nweight: " + weight);
        System.out.println("limit: " + limit);
        System.out.println("params: " + params.subList(0, params.size() - 1));
        System.out.println("condition: " + conditionSignal + "" + cst);
        System.out.println("\n\n\n------- DOING CALCULATION -------\n\n");

        // 前提：自变量取值满足条件
        possibleVars.add(Arrays.asList(5, 5, 1));
        possibleVars.add(Arrays.asList(5, 10, 0));
        possibleVars.add(Arrays.asList(6, 4, 0));
        possibleVars.forEach(list -> System.out.println("possibleVar: " + list));

        System.out.println("------- DOING CALCULATION -------");
        int bestIndex = 0;
        double result = 0.0;
        for (int i = 0; i < possibleVars.size(); i++) {
            double y = 0.0;
            for (int j = 0; j < weight.size(); j++) {
                y += weight.get(j) * possibleVars.get(i).get(j);
            }
            goalValues.add(y);
            if (result < y) {
                result = y;
                bestIndex = i;
            }
            System.out.println("solve - " + (i + 1) + ": " + y);
        }
        System.out.println("\n\nbest solution: " + possibleVars.get(bestIndex));
        System.out.println("best gain: " + result);
        return bestIndex;
    }

    public Boolean compare(Number a, String s, Number b) {
        switch (s) {
            case "eq":
                if (a.doubleValue() != b.doubleValue()) return false;
                break;
            case "ge":
                if (a.doubleValue() < b.doubleValue()) return false;
                break;
            case "le":
                if (a.doubleValue() > b.doubleValue()) return false;
                break;
            case "lt":
                if (a.doubleValue() >= b.doubleValue()) return false;
                break;
            case "gt":
                if (a.doubleValue() <= b.doubleValue()) return false;
                break;
            case "ne":
                if (a.doubleValue() == b.doubleValue()) return false;
                break;
            default:
                return null;
        }
        return true;
    }

    public List<Double> getGoalValues() {
        if (possibleVars.isEmpty()) {
            System.out.println("------ 变量未计算！");
            return null;
        }
        if (goalValues != null) {
            return goalValues;
        }
        List<Double> result = new LinkedList<>();
        for (List<Integer> vars : possibleVars) {
            double temp = 0.0;
            for (int i = 0; i < weight.size(); i++) {
                temp = weight.get(i) * vars.get(i);
            }
            result.add(temp);
        }
        goalValues = result;
        return result;
    }

    public void doCalculate() {
        // TODO：计算
        for (int i = 0; i < conditions.size(); i++) {
            List<Integer> vars = initVar();
            List<Double> params = conditions.get(i).keySet().iterator().next();
            double cst = params.get(params.size() - 1);
            double y = 0.0;
            for (int j = 0; j < vars.size(); j++) {
                y += vars.get(i) * params.get(i);
            }
            switch (conditions.get(i).get(params)) {
                case "eq":
                    if (y != cst) {
                        System.out.println("解集: " + vars + "不符合条件！");
                        continue;
                    } else {
                        break;
                    }
                case "ge":
                    if (y >= cst) {
                        System.out.println("解集: " + vars + "不符合条件！");
                        continue;
                    } else {
                        break;
                    }
                case "le":
                    if (y <= cst) {
                        System.out.println("解集: " + vars + "不符合条件！");
                        continue;
                    } else {
                        break;
                    }
                case "lt":
                    if (y < cst) {
                        System.out.println("解集: " + vars + "不符合条件！");
                        continue;
                    } else {
                        break;
                    }
                case "gt":
                    if (y > cst) {
                        System.out.println("解集: " + vars + "不符合条件！");
                        continue;
                    } else {
                        break;
                    }
                case "ne":
                    if (y == cst) {
                        System.out.println("解集: " + vars + "不符合条件！");
                        continue;
                    } else {
                        break;
                    }
                default:
                    break;
            }
        }
    }

    private List<Integer> initVar() {
        List<Integer> vars = new ArrayList<>();
        limit.forEach((signal, value) -> {
            // eq: =   lt: <   gt: >   le: <=   ge: >=  ne: !=
            // 假设传入全为整数
            int val = (int) value.doubleValue();
            switch (signal.substring(signal.length() - 2)) {
                case "eq":
                case "ge":
                case "le":
                    vars.add(val);
                    break;
                case "lt":
                    vars.add(val - 1);
                    break;
                case "gt":
                case "ne":
                    vars.add(val + 1);
                    break;
                default:
                    break;
            }
        });
        possibleVars.add(vars);
        return vars;
    }


}
