package xyz.cofe.mapitf;

import java.util.List;

public interface RefCompaund {
    String str();
    RefCompaund str(String num);

    List<Simple> simples();

    @Ctor Simple createSimple();
    @Ctor RefCompaund createRef();

    List<RefCompaund> refs();
}
