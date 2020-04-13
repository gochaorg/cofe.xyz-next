package xyz.cofe.cbuffer.page;

/**
 * Указывает конфигурацию по умолчанию
 */
public class PageConf {
    /**
     * Системное свойство cbuffer.page.PageSizePropertyHolder = true указывает использование
     * WeakHashMap для храения данных в {@link PageSizePropertyHolder}
     * @return false - по умолчанию; true - использовать WeakHashMap для храения размера страниц
     */
    public static boolean weakPageSize(){
        return "true".equalsIgnoreCase(
            System.getProperty("cbuffer.page.PageSizePropertyHolder", "false")
        );
    }

    /**
     * Системное свойство cbuffer.page.PageBuffersPropertyHolder.fast = true указывает использование
     * WeakHashMap для храения данных в {@link PageBuffersPropertyHolder}
     * @return false - по умолчанию; true - использовать WeakHashMap для храения данных
     */
    public static boolean fastWeakBufferReference(){
        // PageBuffersPropertyHolder
        return "true".equalsIgnoreCase(
            System.getProperty("cbuffer.page.PageBuffersPropertyHolder.fast", "false")
        );
    }

    /**
     * Системное свойство cbuffer.page.PageBuffersPropertyHolder.slow = true указывает использование
     * WeakHashMap для храения данных в {@link PageBuffersPropertyHolder}
     * @return false - по умолчанию; true - использовать WeakHashMap для храения данных
     */
    public static boolean slowWeakBufferReference(){
        // PageBuffersPropertyHolder
        return "true".equalsIgnoreCase(
            System.getProperty("cbuffer.page.PageBuffersPropertyHolder.slow", "false")
        );
    }
}
