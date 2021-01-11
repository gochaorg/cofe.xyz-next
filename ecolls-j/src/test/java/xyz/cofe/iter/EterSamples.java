package xyz.cofe.iter;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import org.junit.Test;
import xyz.cofe.iter.Eterable;

public class EterSamples {
    @SuppressWarnings("unchecked")
    @Test
    public void sample1(){
        Eterable<Integer> seq1 = Eterable.single( 1 ).union( Eterable.of( 2, 3, 4, 5 ) );
        seq1.product( seq1).map( p -> p.a() * p.b() ).filter( x -> x % 3 == 0 ).forEach( System.out::println );
    }

    @Test
    public void sample2(){
        File rootDirectory = new File(".");
        Eterable.tree(
            // задаем корень
            rootDirectory,

            // задаем функцию перехода от текущего узла к дочерним
            dir -> dir.isDirectory() ? Arrays.asList(
                Objects.requireNonNull(dir.listFiles())
            ) : null
        ).walk().forEach( System.out::println );
    }

    @Test
    public void sample3(){
        File rootDirectory = new File(".");

        Eterable<TreeStep<File>> iter = Eterable.tree(
            // задаем корень
            rootDirectory,

            // задаем функцию перехода от текущего узла к дочерним
            dir -> dir.isDirectory() ? Arrays.asList(
                Objects.requireNonNull(dir.listFiles())
            ) : null
        ).go();

        iter = iter.filter( step -> {
            String path = step.nodes().map(File::getName).reduce("", (a, b)->a + "/" +b);
//            System.out.println("path "+path);
            return path.startsWith("/./src/main/java/xyz/cofe/fn/Fn");
        });

        System.out.println("files "+iter.count());
        for( TreeStep<File> ts : iter ){
            System.out.println("file "+ts.getNode());
        }
    }
}
