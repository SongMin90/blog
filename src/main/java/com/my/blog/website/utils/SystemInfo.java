package com.my.blog.website.utils;

import com.alibaba.fastjson.JSONObject;
import org.hyperic.sigar.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 系统信息
 *
 * @author SongM
 */
public class SystemInfo {

    public static JSONObject SystemProperty() {
        JSONObject monitorMap = new JSONObject();
        Runtime r = Runtime.getRuntime();
        Properties props = System.getProperties();
        InetAddress addr = null;
        String ip = "";
        String hostName = "";
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            ip = "无法获取主机IP";
            hostName = "无法获取主机名";
        }
        if (null != addr) {
            try {
                ip = addr.getHostAddress();
            } catch (Exception e) {
                ip = "无法获取主机IP";
            }
            try {
                hostName = addr.getHostName();
            } catch (Exception e) {
                hostName = "无法获取主机名";
            }
        }
        // 本地ip地址
        monitorMap.put("hostIp", ip);
        // 本地主机名
        monitorMap.put("hostName", hostName);
        // 操作系统的名称
        monitorMap.put("osName", props.getProperty("os.name"));
        // 操作系统的构架
        monitorMap.put("arch", props.getProperty("os.arch"));
        // 操作系统的版本
        monitorMap.put("osVersion", props.getProperty("os.version"));
        // JVM可以使用的处理器个数
        monitorMap.put("processors", r.availableProcessors());
        // Java的运行环境版本
        monitorMap.put("javaVersion", props.getProperty("java.version"));
        // Java的运行环境供应商
        monitorMap.put("vendor", props.getProperty("java.vendor"));
        // Java供应商的URL
        monitorMap.put("javaUrl", props.getProperty("java.vendor.url"));
        // Java的安装路径
        monitorMap.put("javaHome", props.getProperty("java.home"));
        // 默认的临时文件路径
        monitorMap.put("tmpdir", props.getProperty("java.io.tmpdir"));
        return monitorMap;
    }

    private static String div(long num) {
        long gb = 1024 * 1024 * 1024;
        long mb = 1024 * 1024;
        long kb = 1024;
        if (num > gb) {
            return (BigDecimal.valueOf(num).divide(BigDecimal.valueOf(gb),2, RoundingMode.HALF_UP)) + "G";
        } else if (num > mb) {
            return (BigDecimal.valueOf(num).divide(BigDecimal.valueOf(mb),2, RoundingMode.HALF_UP)) + "M";
        } else if (num > kb) {
            return (BigDecimal.valueOf(num).divide(BigDecimal.valueOf(kb),2, RoundingMode.HALF_UP)) + "K";
        }
        return String.valueOf(num);
    }

    private static String div1(long num) {
        long gb = 1024 * 1024;
        long mb = 1024;
        if (num > gb) {
            return (BigDecimal.valueOf(num).divide(BigDecimal.valueOf(gb),2, RoundingMode.HALF_UP)) + "G";
        } else if (num > mb) {
            return (BigDecimal.valueOf(num).divide(BigDecimal.valueOf(mb),2, RoundingMode.HALF_UP)) + "M";
        }
        return String.valueOf(num);
    }

    public static JSONObject memory(Sigar sigar) {
        JSONObject monitorMap = new JSONObject();
        try {
            Runtime r = Runtime.getRuntime();
            // java总内存
            monitorMap.put("jvmTotal", div(r.totalMemory()));
            // JVM剩余内存
            monitorMap.put("jvmFree", div(r.freeMemory()));
            // JVM使用内存
            long useMemory = r.totalMemory() - r.freeMemory();
            monitorMap.put("jvmUse", div(useMemory));
            // JVM使用率
            monitorMap.put("jvmUsage", Math.round(Double.valueOf(useMemory) / Double.valueOf(r.totalMemory()) * 100) + "%");

            Mem mem = sigar.getMem();
            // 内存总量
            monitorMap.put("ramTotal", div(mem.getTotal()));
            // 当前内存使用量
            monitorMap.put("ramUse", div(mem.getUsed()));
            // 当前内存剩余量
            monitorMap.put("ramFree", div(mem.getFree()));
            // 内存使用率
            monitorMap.put("ramUsage", Math.round(Double.valueOf(mem.getUsed()) / Double.valueOf(mem.getTotal()) * 100) + "%");

            Swap swap = sigar.getSwap();
            // 交换区总量
            monitorMap.put("swapTotal", div(swap.getTotal()));
            // 当前交换区使用量
            monitorMap.put("swapUse", div(swap.getUsed()));
            // 当前交换区剩余量
            monitorMap.put("swapFree", div(swap.getFree()));
            // 交换率
            monitorMap.put("swapUsage", Math.round(Double.valueOf(swap.getUsed()) / Double.valueOf(swap.getTotal()) * 100) + "%");
        } catch (Exception e) {
        }
        return monitorMap;
    }

    public static List<JSONObject> cpuInfos(Sigar sigar) {
        List<JSONObject> monitorMaps = new ArrayList<JSONObject>();
        try {
            CpuPerc[] cpuList = sigar.getCpuPercList();
            for (CpuPerc cpuPerc : cpuList) {
                JSONObject monitorMap = new JSONObject();
                // 用户使用率
                monitorMap.put("cpuUserUse", Math.round(cpuPerc.getUser()*100) + "%");
                // 系统使用率
                monitorMap.put("cpuSysUse", Math.round(cpuPerc.getSys()*100) + "%");
                // 当前等待率
                monitorMap.put("cpuWait", Math.round(cpuPerc.getWait()*100) + "%");
                // 当前空闲率
                monitorMap.put("cpuFree", Math.round(cpuPerc.getIdle()*100) + "%");
                // 总的使用率
                monitorMap.put("cpuTotal", Math.round(cpuPerc.getCombined()*100) + "%");
                monitorMaps.add(monitorMap);
            }
        } catch (Exception e) {
        }
        return monitorMaps;
    }

    public static List<JSONObject> diskInfos(Sigar sigar) {
        List<JSONObject> monitorMaps = new ArrayList<>();
        try {
            FileSystem[] fsList = sigar.getFileSystemList();
            for (int i = 0; i < fsList.length; i++) {
                JSONObject monitorMap = new JSONObject();
                FileSystem fs = fsList[i];
                // 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
                FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
                switch (fs.getType()) {
                    // TYPE_UNKNOWN ：未知
                    case 0:
                        break;
                    // TYPE_NONE
                    case 1:
                        break;
                    // TYPE_LOCAL_DISK : 本地硬盘
                    case 2:
                        // 系统盘名称
                        monitorMap.put("diskName", fs.getDevName());
                        // 盘类型
                        monitorMap.put("diskType", fs.getSysTypeName());
                        // 文件系统总大小
                        monitorMap.put("diskTotal", div1(usage.getTotal()));
                        // 文件系统剩余大小
                        monitorMap.put("diskFree", div1(usage.getFree()));
                        // 文件系统已经使用量
                        monitorMap.put("diskUse", div1(usage.getUsed()));
                        // 内存使用率
                        monitorMap.put("diskUsage", Math.round(usage.getUsePercent() * 100) + "%");
                        // read
                        monitorMap.put(fs.getDevName() + "read", usage.getDiskReads());
                        // write
                        monitorMap.put(fs.getDevName() + "write", usage.getDiskWrites());
                        monitorMaps.add(monitorMap);
                        break;
                    // TYPE_NETWORK ：网络
                    case 3:
                        break;
                    // TYPE_RAM_DISK ：闪存
                    case 4:
                        break;
                    // TYPE_CDROM ：光驱
                    case 5:
                        break;
                    // TYPE_SWAP ：页面交换
                    case 6:
                        break;
                    default:
                        break;
                }
            }
        } catch (SigarException e) {
        }
        return monitorMaps;
    }

    public static void main(String[] args) {
        JSONObject serverinfo = SystemInfo.SystemProperty();
        Sigar sigar = new Sigar();
        JSONObject memory = SystemInfo.memory(sigar);
        List<JSONObject> cpuInfos = SystemInfo.cpuInfos(sigar);
        List<JSONObject> diskInfos = SystemInfo.diskInfos(sigar);
    }
}
