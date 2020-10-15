package xyz.cofe.mapitf;

import java.util.function.Consumer;

public interface OffScnChanged {
    void offScnChanged( Consumer<? super GetScn> changed );
}
