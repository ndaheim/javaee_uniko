package echo.models;

import echo.util.I18nUtil;

public enum FederalState {
    BREMEN("br"),
    HAMBURG("hh"),
    BERLIN("be"),
    SAARLAND("sl"),
    SCHLESWIG_HOLSTEIN("sh"),
    THURINGIA("th"),
    SAXONY("sn"),
    RHINELAND_PALATINATE("rp"),
    SAXONY_ANHALT("st"),
    HESSE("he"),
    MECKLENBURG_WESTERN_POMERANIA("mv"),
    BRANDENBURG("bb"),
    NORTHRHINE_WESTPHALIA("nw"),
    BADEN_WUERTTEMBERG("bw"),
    LOWER_SAXONY("ls"),
    BAVARIA("by");

    private String abbreviation;

    FederalState(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getLocalizedName() {
        return I18nUtil.getString("federal.state." + name().toLowerCase());
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
