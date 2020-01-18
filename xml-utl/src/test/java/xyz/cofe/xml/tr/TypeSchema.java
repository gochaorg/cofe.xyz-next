package xyz.cofe.xml.tr;

import xyz.cofe.typeconv.TypeCastGraph;

public class TypeSchema {
//    private TypeCastGraph typeCast;
//    public TypeCastGraph getTypeCast(){ return typeCast; }
//    public void setTypeCast( TypeCastGraph typeCast ){ this.typeCast = typeCast; }

    public boolean isAtomic( Class cls ){
        if( cls==null ) throw new IllegalArgumentException("cls==null");
        if( Number.class.isAssignableFrom(cls) )return true;
        if( CharSequence.class.isAssignableFrom(cls) )return true;
        if( Boolean.class.isAssignableFrom(cls) )return true;
        if( cls.isPrimitive() )return true;

//        TypeCastGraph tc = typeCast;
//        if( tc!=null && tc.contains(cls) )return true;

        return false;
    }

    //public <T> T atomic( Class<T> atomCls,  )
}
