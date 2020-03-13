package xyz.cofe.text.tparse;

import java.util.Optional;
import java.util.function.Predicate;

public class Chars {
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

    public static final GR<CharPointer,CToken> digit = test(Character::isDigit);
    public static final GR<CharPointer,CToken> letter = test(Character::isLetter);
    public static final GR<CharPointer,CToken> letterOrDigit = test(Character::isLetterOrDigit);
    public static final GR<CharPointer,CToken> whitespace = test(Character::isWhitespace);
}
