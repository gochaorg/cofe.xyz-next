package xyz.cofe.text.parse;

import xyz.cofe.text.parse.tmpl.Template;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Функция проверки принадлежности символа к классу символов
 */
public enum CharType implements Function<CharPointer,Token> {
    UNDEFINED( x-> false ),
    DIGIT_DOT( c -> c=='.' ),

    Whitespace(
        Character::isWhitespace,
        "Character.isWhitespace( ${ptr}.lookup() ) ? new "+Token.class.getName()+"( ${ptr}, ${ptr}.move(1) )"
    ),

    Digit(
        Character::isDigit,
        "Character.isDigit( ${ptr}.lookup() ) ? new "+Token.class.getName()+"( ${ptr}, ${ptr}.move(1) )"
    ),

    Letter(
        Character::isLetter,
        "Character.isLetter( ${ptr}.lookup() ) ? new "+Token.class.getName()+"( ${ptr}, ${ptr}.move(1) )"
    ),

    LetterOrDigit(
        Character::isLetterOrDigit,
        "Character.isLetterOrDigit( ${ptr}.lookup() ) ? new "+Token.class.getName()+"( ${ptr}, ${ptr}.move(1) )"
    ),

    Alphabetic(
        Character::isAlphabetic,
        "Character.isAlphabetic( ${ptr}.lookup() ) ? new "+Token.class.getName()+"( ${ptr}, ${ptr}.move(1) )"
    ),

    BmpCodePoint(Character::isBmpCodePoint),
    Defined(Character::isDefined),
    HighSurrogate(Character::isHighSurrogate),
    IdentifierIgnorable(Character::isIdentifierIgnorable),
    Ideographic(Character::isIdeographic),
    ISOControl(Character::isISOControl),
    JavaIdentifierPart(Character::isJavaIdentifierPart),
    JavaIdentifierStart(Character::isJavaIdentifierStart),
    LowerCase(Character::isLowerCase),
    LowSurrogate(Character::isLowSurrogate),
    Mirrored(Character::isMirrored),
    TitleCase(Character::isTitleCase),
    SpaceChar(Character::isSpaceChar),
    SupplementaryCodePoint(Character::isSupplementaryCodePoint),
    UnicodeIdentifierPart(Character::isUnicodeIdentifierPart),
    UnicodeIdentifierStart(Character::isUnicodeIdentifierStart),
    UpperCase(Character::isUpperCase),
    ValidCodePoint(Character::isValidCodePoint),
    //JavaLetterOrDigit(Character::isJavaLetterOrDigit),
    //JavaLetter(Character::isJavaLetter),
    Surrogate(Character::isSurrogate);

    private Predicate<Character> testfn;
    private final Template template;

    CharType( Predicate<Character> testfn ){
        if( testfn == null ) throw new IllegalArgumentException("testfn==null");
        this.testfn = testfn;
        template = null;
    }

    CharType( Predicate<Character> testfn, String template ){
        if( testfn == null ) throw new IllegalArgumentException("testfn==null");
        this.testfn = testfn;

        if( template!=null ){
            this.template = Template.parse(template);
        }else{
            this.template = null;
        }
    }

    public boolean test( Character c ){
        if( c==null ) c = (Character)(char)0;
        return testfn.test(c);
    }

    public boolean test( char c ){
        return testfn.test(c);
    }
    public boolean test( int c ){
        return test((char)c);
    }

    @Override
    public Token apply( CharPointer tp ){
        if( tp==null )return null;
        if( tp.eof() )return null;
        if( test(tp.lookup()) )return new Token(tp, tp.move(1));
        return null;
    }

    public String exprTemplate( String textPointerVarName ){
        if( textPointerVarName==null ) throw new IllegalArgumentException("textPointerVarName==null");
        if( template==null )return null;

        HashMap m = new HashMap();
        m.put("ptr",textPointerVarName);

        return template.apply(m);
    }
}
