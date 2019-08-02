package xyz.cofe.io.fs.typeconv;

import xyz.cofe.io.fs.File;
import xyz.cofe.typeconv.spi.GetTypeConvertor;

import java.util.function.Function;

public class CofeIOFile2File implements GetTypeConvertor {
    @Override
    public Class getSourceType(){
        return File.class;
    }

    @Override
    public Class getTargetType(){
        return java.io.File.class;
    }

    private Function fn = from -> ((File)from).toFile();

    @Override
    public Function<Object, Object> getConvertor(){
        return fn;
    }
}
