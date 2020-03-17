package xyz.cofe.text.tparse;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Предопределенные классы симвлов
 */
public class Chars {
    /**
     * Создает грамматическое правило из предиката
     * @param filter предикат
     * @return правило
     */
    public static GR<CharPointer,CToken> test(Predicate<Character> filter){
        if( filter==null )throw new IllegalArgumentException("filter == null");
        return new GR<CharPointer, CToken>() {
            @Override
            public Optional<CToken> apply(CharPointer ptr) {
                if( ptr==null )throw new IllegalArgumentException("ptr==null");

                Optional<Character> chr = ptr.lookup(0);
                if( chr==null )return Optional.empty();
                if( !chr.isPresent() )return Optional.empty();

                if( !filter.test(chr.get()) )return Optional.empty();

                return Optional.of( new CToken(ptr, ptr.move(1)) );
            }
        };
    }

    /**
     * Правило - символ относится к цифре
     */
    public static final GR<CharPointer,CToken> digit = test(Character::isDigit);

    /**
     * Правило - символ относится к букве
     */
    public static final GR<CharPointer,CToken> letter = test(Character::isLetter);

    /**
     * Правило - символ относится к цифре или букве
     */
    public static final GR<CharPointer,CToken> letterOrDigit = test(Character::isLetterOrDigit);

    /**
     * Правило - символ относится к пробельному
     */
    public static final GR<CharPointer,CToken> whitespace = test(Character::isWhitespace);
}
