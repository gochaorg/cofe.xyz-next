package xyz.cofe.text.tparse;

public interface Tok<P extends Pointer<?,?,P>> {
    P begin();
    P end();
}
