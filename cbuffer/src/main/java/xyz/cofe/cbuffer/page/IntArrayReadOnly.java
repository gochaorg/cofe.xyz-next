package xyz.cofe.cbuffer.page;

/**
 * Интерфейс чтения массива
 */
public interface IntArrayReadOnly {
    /**
     * Возвращает Размер массива
     * @return Размер массива
     */
    int length();

    /**
     * Чтение элемента массива
     * @param index элемент
     * @return значение
     */
    int get(int index);

    default int[] toArray(){
        int[] arr = new int[length()];
        for( int i=0; i<arr.length; i++ ){
            arr[i] = get(i);
        }
        return arr;
    }

    /**
     * Обвертка над массивом
     * @param arr массив
     * @return обвертка
     */
    static IntArrayReadOnly of(int[] arr){
        if( arr==null )throw new IllegalArgumentException( "arr==null" );
        return new IntArrayReadOnly() {
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
