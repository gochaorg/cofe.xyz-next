package xyz.cofe.text.parse.mtest;

import xyz.cofe.collection.ImTree;
import xyz.cofe.collection.ImTreeWalk;
import xyz.cofe.text.parse.MathTest;
import xyz.cofe.text.parse.Tok;
import xyz.cofe.text.parse.TokenPointer;

public interface Expr extends Tok<TokenPointer>, ImTree<Expr>, ImTreeWalk<Expr> {
    TokenPointer getBegin();
}