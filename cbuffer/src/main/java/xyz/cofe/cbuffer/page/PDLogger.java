package xyz.cofe.cbuffer.page;

import xyz.cofe.ecolls.Closeables;
import xyz.cofe.fn.Pair;

import java.io.Closeable;
import java.io.IOException;

public class PDLogger implements Closeable {
    private final Closeables closeables = new Closeables();
    private Appendable out;

    private void log(String message) {
        synchronized (closeables) {
            if (out != null) {
                try {
                    out.append("ev: ").append(message).append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PDLogger(PageDataImpl pd, Appendable out) {
        if (pd == null) throw new IllegalArgumentException("pd==null");
        if (out == null) throw new IllegalArgumentException("out==null");

        this.out = out;

        closeables.append(
            pd.onMapped.listen(e -> {
                    Object[] arr = pd.getDirtyPages().stream().toArray();

                    log(
                        "map fast=" + e.a() + "=slow=>" + e.b() +
                            " fast.cnt=" + pd.fastPageCount() + "/" + pd.getMaxFastPageCount() + "max" +
                            " dirty.cnt=" + pd.getDirtyPageCount() +
                            "[" + pd.getDirtyPages().stream()
                            .map(Pair::a)
                            //.map(Objects::toString)
                            .reduce("", (a, b) -> a + (a.length() > 0 ? "," : "") + b, (a, b) -> a + b)
                            + "]");
                }
            ),
            pd.onFastDataWrited.listen(e -> log("fast writed page=" + e.a() + " " + e.b().length + " bytes")),
            pd.onDirty.listen(e -> log("dirty page=" + e.a() + " dirty=" + e.b())),
            pd.onAllocNewPage.listen(e -> log("alloc new page " + e)),
            pd.onAllocFreePage.listen(e -> log("alloc free page " + e)),
            pd.onAllocExistsPage.listen(e -> log("alloc exists page " + e)),
            pd.onFastDataSize.listen(e -> log("page " + e.a() + " size " + e.b())),
            pd.onAlloc.listen(e -> log("alloc " + e)),
            pd.onSavedFastPage.listen(e -> log("save " + e))
        );
    }

    @Override
    public void close() {
        synchronized (closeables) {
            closeables.close();
        }
    }
}
