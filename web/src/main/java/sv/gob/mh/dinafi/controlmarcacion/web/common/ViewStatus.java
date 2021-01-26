package sv.gob.mh.dinafi.controlmarcacion.web.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ViewStatus {

    NONE(0, "NONE"),
    NEW(1, "NEW"),
    EDIT(2, "EDIT"),
    REMOVE(3, "REMOVE"),
    VIEW(4, "VIEW");
    private final int code;
    private final String description;

    public static ViewStatus getEnumConstant(final int code) {
        ViewStatus ret = null;
        for (ViewStatus ienum : values()) {
            if (ienum.getCode() == code) {
                ret = ienum;
                break;
            }
        }
        return ret;
    }
}
