package xyz.cofe.io.fs.typeconv;

import xyz.cofe.typeconv.spi.GetTypeConvertor;

import java.util.function.Function;

public class CofeIOFile2String implements GetTypeConvertor {
    @Override
    public Class getSourceType(){
        return xyz.cofe.io.fs.File.class;
    }

    @Override
    public Class getTargetType(){
        return String.class;
    }

    private Function<Object, Object> fn = from -> {
        xyz.cofe.io.fs.File file = (xyz.cofe.io.fs.File)from;
        return file.toString();
    };

    @Override
    public Function<Object, Object> getConvertor(){
        return fn;
    }
}
