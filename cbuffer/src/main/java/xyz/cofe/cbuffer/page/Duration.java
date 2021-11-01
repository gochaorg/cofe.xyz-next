package xyz.cofe.cbuffer.page;

public interface Duration<SELF extends Duration> {
    SELF add(SELF self);
    SELF sub(SELF self);
}
