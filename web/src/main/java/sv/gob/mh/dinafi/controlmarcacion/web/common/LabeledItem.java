package sv.gob.mh.dinafi.controlmarcacion.web.common;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"label"})
public class LabeledItem<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -4689769966455436723L;

    private String label;
    private T value;

    public LabeledItem(T value) {
        this("" + value, value);
    }

    @Override
    public String toString() {
        return "" + value;
    }

}
