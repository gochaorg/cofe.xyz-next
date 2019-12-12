package xyz.cofe.text.parse;

import java.util.List;

/**
 * Указатель на лексемы
 */
public class BasicTokenPointer implements TokenPointer {
    private final List<Token> tokens;
    private final int pointer;

    public BasicTokenPointer( List<Token> tokens, int initial ){
        if( tokens==null ) throw new IllegalArgumentException("tokens==null");
        this.pointer = initial;
        this.tokens = tokens;
    }

    public BasicTokenPointer( BasicTokenPointer sample, int initial ){
        if( sample==null ) throw new IllegalArgumentException("sample==null");
        this.pointer = initial;
        this.tokens = sample.tokens;
    }

    @Override
    public Integer pointer(){
        return this.pointer;
    }

    @Override
    public Token lookup(){
        if( pointer<0 )return null;
        if( pointer>=tokens.size() )return null;
        return tokens.get(pointer);
    }

    @Override
    public TokenPointer move( int offset ){
        return new BasicTokenPointer(this, pointer+offset);
    }

    @Override
    public boolean eof(){
        return pointer >= tokens.size();
    }

    @Override
    public int compareTo( Pointer o ){
        if( o==null )return 0;
        if( o instanceof TokenPointer ){
            int p2 = ((TokenPointer) o).pointer();
            return pointer == p2 ? 0 : (
                pointer < p2 ? -1 : 1
            );
        }
        return 0;
    }
}
