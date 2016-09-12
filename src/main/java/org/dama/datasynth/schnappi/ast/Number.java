package org.dama.datasynth.schnappi.ast;

import org.dama.datasynth.common.Types;

/**
 * Created by aprat on 24/08/16.
 */
public class Number extends Literal {

    private Types.DataType dataType = null;

    public Number(String value, Types.DataType type) {
        super(value);
        this.dataType = type;
    }

    public Number(Number n) {
        super(n);
        this.dataType = n.dataType;
    }

    public Object getObject() {
        if(dataType == Types.DataType.LONG) {
            return Long.parseLong(getValue());
        } else if(dataType == Types.DataType.DOUBLE) {
            return Double.parseDouble(getValue());
        }
        return null;
    }

    public Types.DataType getDataType() {
        return dataType;
    }

    @Override
    public Number copy() {
        return new Number(this);
    }

    @Override
    public java.lang.String toString() {
        return "<"+dataType.getText()+","+value+">";
    }
}
