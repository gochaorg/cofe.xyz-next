package xyz.cofe.cbuffer.stat;

public interface Duration<SELF extends Duration> {
    SELF add(SELF self);
    SELF sub(SELF self);
}
