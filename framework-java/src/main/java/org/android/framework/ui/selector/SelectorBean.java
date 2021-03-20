package org.android.framework.ui.selector;

import java.io.Serializable;

public interface SelectorBean extends Serializable {

    String getIValue();

    String getILabel();

    static SelectorBean create(String label) {
        return new SelectorBean() {
            @Override
            public String getIValue() {
                return null;
            }

            @Override
            public String getILabel() {
                return label;
            }
        };
    }

    static SelectorBean create(String value, String label) {
        return new SelectorBean() {
            @Override
            public String getIValue() {
                return value;
            }

            @Override
            public String getILabel() {
                return label;
            }
        };
    }
}
