package xyz.cofe.mapitf;

import java.util.List;

public interface Compaund {
    List<Simple> simples();

    @Ctor
    Simple create();
}
