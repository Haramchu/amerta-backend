package propensi.amesta.service;

import java.util.List;

public class JoinInfo {
    public String field;
    public List<String> joins;

    public JoinInfo(String field, List<String> joins) {
        this.field = field;
        this.joins = joins;
    }
}