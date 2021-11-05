package xyz.cofe.cbuffer.page;

public interface IntArrayMutable extends IntArrayReadOnly {
    void set(int index,int value);
    static IntArrayMutable of( int[] arr ){
        if( arr==null )throw new IllegalArgumentException( "arr==null" );
        return new IntArrayMutable() {
            @Override
            public void set(int index, int value) {
                arr[index] = value;
            }

            @Override
            public int length() {
                return arr.length;
            }

            @Override
            public int get(int index) {
                return arr[index];
            }
        };
    }
}
