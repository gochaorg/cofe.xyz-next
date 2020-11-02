package xyz.cofe.mapitf;

import java.lang.reflect.Method;
import java.util.Optional;

public interface PropertyMapper {
    Optional<String> map(Method method);
    static PropertyMapper asis(){
        return new PropertyMapper() {
            @Override
            public Optional<String> map(Method method) {
                if( method==null )return Optional.empty();
                return Optional.of(method.getName());
            }
        };
    }

    static PropertyMapper beans(){
        return new PropertyMapper() {
            @Override
            public Optional<String> map(Method method) {
                if( method==null )return Optional.empty();
                String mname = method.getName();
                //noinspection ConstantConditions
                if( mname==null )return Optional.empty();

                String pname = null;
                if( (mname.startsWith("get") || mname.startsWith("set")) && mname.length()>3 ){
                    pname = mname.substring(3);
                }else if( mname.startsWith("is") && mname.length()>2 ){
                    pname = mname.substring(2);
                }
                if( pname!=null ){
                    if( pname.length()>1 ){
                        if( Character.isUpperCase(pname.charAt(0)) && Character.isLowerCase(pname.charAt(1)) ){
                            return Optional.of("" + Character.toLowerCase(pname.charAt(0)) + pname.substring(1));
                        }else {
                            return Optional.of(pname);
                        }
                    }else{
                        if( Character.isUpperCase(pname.charAt(0)) ){
                            return Optional.of(pname.toLowerCase());
                        }else{
                            return Optional.of(pname);
                        }
                    }
                }
                return Optional.empty();
            }
        };
    }

    static PropertyMapper common(){
        PropertyMapper pmBeans = beans();
        PropertyMapper pmAsis = asis();
        return new PropertyMapper() {
            @Override
            public Optional<String> map(Method method) {
                if( method==null )return Optional.empty();

                Optional<String> name1 = pmBeans.map(method);
                if( name1.isPresent() )return name1;

                return pmAsis.map(method);
            }
        };
    }
}
