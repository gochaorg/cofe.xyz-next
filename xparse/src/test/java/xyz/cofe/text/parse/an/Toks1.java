package xyz.cofe.text.parse.an;

import xyz.cofe.text.parse.toks.IdToken;

import static xyz.cofe.text.parse.CharPredicates.*;
import static xyz.cofe.text.parse.an.JoinOp.*;

public interface Toks1 {
    IdToken id(
        @CharClasses(Letter)
        char begin,

        @Join(OR)
        @CharClasses({Letter,Digit})
        char next
    );
}
