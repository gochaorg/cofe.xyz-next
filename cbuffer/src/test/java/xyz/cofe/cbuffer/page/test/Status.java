package xyz.cofe.cbuffer.page.test;

import xyz.cofe.io.fn.IOFun;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://man7.org/linux/man-pages/man5/proc.5.html
public class Status {
    private Map<String, String> status;
    public Map<String, String> status(){
        return status;
    }

    public Status(Map<String, String> status) {
        if (status == null) throw new IllegalArgumentException("status");
        this.status = status;
    }

    private static BigInteger kb = BigInteger.valueOf(1024);
    private static BigInteger mb = BigInteger.valueOf(1024*1024);
    private static BigInteger gb = BigInteger.valueOf(1024*1024*1024);

    public class MemUse {
        public final String key;
        public MemUse(String key){
            if( key==null )throw new IllegalArgumentException("key==null");
            this.key = key;
        }

        public Optional<BigInteger> value(){
            String v = status.get(key);
            if( v==null )return Optional.empty();
            return size(v);
        }

        public Optional<Long> asKB(){
            return value().map( v -> v.divide(BigInteger.valueOf(1024)).longValue() );
        }

        public Optional<Long> asMB(){
            return value().map( v -> v.divide(BigInteger.valueOf(1024*1024)).longValue() );
        }

        public String toHumanReadable(){
            Optional<BigInteger> num_opt = value();
            if( num_opt.isEmpty() )return key+" n/a";

            BigInteger num = num_opt.get();
            if( num.compareTo(gb)>0 ){
                BigInteger gb_v = num.divide(gb);
                if( gb_v.compareTo(BigInteger.TEN)>0 ) {
                    return key + " " + gb_v + " Gb";
                }else{
                    BigInteger ost_b = num.subtract( gb_v.multiply(gb) );
                    BigInteger ost_mb = ost_b.divide(mb);
                    return key + " " + gb_v + " Gb "+ost_mb+" Mb";
                }
            }else if( num.compareTo(mb)>0 ){
                num = num.divide(mb);
                return key+" "+num+" Mb";
            }else if( num.compareTo(kb)>0 ){
                num = num.divide(kb);
                return key+" "+num+" Kb";
            }else{
                return key+" "+num;
            }
        }
    }

    private static Pattern num_as_is = Pattern.compile("\\d+");
    private static Pattern num_with_suff = Pattern.compile("(?is)(?<val>\\d+)\\s*(?<suf>kb|mb|k|m)\\s*");
    private static Optional<BigInteger> size(String str) {
        if (num_as_is.matcher(str).matches()) {
            return Optional.of(new BigInteger(str));
        } else {
            Matcher m = num_with_suff.matcher(str);
            if (m.matches()) {
                BigInteger value = new BigInteger(m.group("val"));
                String suf = m.group("suf");
                BigInteger k = BigInteger.ONE;
                if (suf.equalsIgnoreCase("kb") || suf.equalsIgnoreCase("k")) {
                    k = BigInteger.valueOf(1024);
                } else if (suf.equalsIgnoreCase("mb") || suf.equalsIgnoreCase("m")) {
                    k = BigInteger.valueOf(1024 * 1024);
                }
                return Optional.of(
                    value.multiply(k)
                );
            }
        }
        return Optional.empty();
    }

    // Peak virtual memory size.
    public final MemUse VmPeak = new MemUse("VmPeak");

    // Virtual memory size.
    public final MemUse VmSize = new MemUse("VmSize");

    // Locked memory size
    public final MemUse VmLck = new MemUse("VmLck");

    // Pinned memory size (since Linux 3.2).  These are
    // pages that can't be moved because something needs
    // to directly access physical memory.
    public final MemUse VmPin = new MemUse("VmPin");

    // Resident set size.  Note that the value here is the
    // sum of RssAnon, RssFile, and RssShmem.  This value
    // is inaccurate; see /proc/[pid]/statm above.
    public final MemUse VmRSS = new MemUse("VmRSS");

    // Size of resident anonymous memory.  (since Linux 4.5).  This value is inaccurate;
    public final MemUse RssAnon = new MemUse("RssAnon");

    public static Status selfStatus() {
        String str;
        try {
            str = IOFun.readText(new File("/proc/self/status"), "utf-8");
        } catch (IOException e) {
            return new Status(Map.of());
        }
        Map<String, String> info = new TreeMap<>();
        for (String line : str.split("\\r?\\n")) {
            String[] kv = line.trim().split("\\s*:\\s*", 2);
            if (kv.length == 2) {
                info.put(kv[0], kv[1]);
            }
        }
        return new Status(info);
    }
}
