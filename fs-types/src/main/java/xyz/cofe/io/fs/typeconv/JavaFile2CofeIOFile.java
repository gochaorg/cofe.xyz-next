package xyz.cofe.io.fs.typeconv;

import xyz.cofe.io.fs.File;
import xyz.cofe.typeconv.spi.GetTypeConvertor;

import java.util.function.Function;

public class JavaFile2CofeIOFile implements GetTypeConvertor {
    @Override
    public Class getSourceType(){
        return java.io.File.class;
    }

    @Override
    public Class getTargetType(){
        return File.class;
    }

    private Function fn = from -> new File(((java.io.File)from).toPath());

    @Override
    public Function<Object, Object> getConvertor(){
        return fn;
    }
}
