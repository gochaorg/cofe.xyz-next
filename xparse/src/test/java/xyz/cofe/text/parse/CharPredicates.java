package xyz.cofe.text.parse;

import java.util.function.Predicate;

/**
 * Функции проверки символов
 */
public enum CharPredicates implements Predicate<Character> {
    UNDEFINED(x->false),
    Whitespace(Character::isWhitespace),
    Digit(Character::isDigit),
    Letter(Character::isLetter),
    Alphabetic(Character::isAlphabetic),
    BmpCodePoint(Character::isBmpCodePoint),
    Defined(Character::isDefined),
    HighSurrogate(Character::isHighSurrogate),
    IdentifierIgnorable(Character::isIdentifierIgnorable),
    Ideographic(Character::isIdeographic),
    ISOControl(Character::isISOControl),
    JavaIdentifierPart(Character::isJavaIdentifierPart),
    JavaIdentifierStart(Character::isJavaIdentifierStart),
    LetterOrDigit(Character::isLetterOrDigit),
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

    CharPredicates( Predicate<Character> testfn){
        if( testfn == null ) throw new IllegalArgumentException("testfn==null");
        this.testfn = testfn;
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
}
