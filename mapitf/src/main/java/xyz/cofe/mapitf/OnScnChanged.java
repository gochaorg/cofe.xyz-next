package xyz.cofe.mapitf;

import java.util.function.Consumer;

public interface OnScnChanged {
    AutoCloseable onScnChanged( Consumer<? super GetScn> changed );
}
