package com.matthew.filem.utils;

import android.os.Environment;

import java.io.File;

public abstract class Constants {
    public static final String OPENTHOS_URI = "content://com.otosoft.tools.myprovider/openthosID";
    public static final String DESKFRAGMENT_TAG = "deskfragment";
    public static final String MUSICFRAGMENT_TAG = "musicfragment";
    public static final String VIDEOFRAGMENT_TAG = "videofragment";
    public static final String PICTRUEFRAGMENT_TAG = "pictruefragment";
    public static final String DOCUMENTFRAGMENT_TAG = "documentfragment";
    public static final String DOWNLOADFRRAGMENT_TAG = "downloadfragment";
    public static final String RECYCLEFRAGMENT_TAG = "recyclefragment";
    public static final String SDSTORAGEFRAGMENT_TAG = "sdstoragefragment";
    public static final String DETAILFRAGMENT_TAG = "detailfragment";
    public static final String SEARCHFRAGMENT_TAG = "searchfragment";
    public static final String ADDRESSFRAGMENT_TAG = "addressfragment";
    public static final long DOUBLE_CLICK_INTERVAL_TIME = 1000; // 1.0 second
    public static final String USBFRAGMENT_TAG = "usbfragment_tag";
    public static final String PERSONALSYSTEMSPACE_TAG = "personalsystemspace_tag";
    public static final String SDSSYSTEMSPACE_TAG = "sdssystemspace_tag";
    public static final String SEARCHSYSTEMSPACE_TAG = "searchsystemspace_tag";
    public static final String PERSONAL_TAG = "MY_SPACE";
    public static final String SEAFILESYSTEMSPACE_TAG = "seafilesystemspace_tag";

    public static final String LEFT_FAVORITES = "left_favorites";
    public static final String SYSTEM_SPACE_FRAGMENT = "system_space_fragment";
    public static final String SD_SPACE_FRAGMENT = "sd_space_fragment";
    public static final String USB_SPACE_FRAGMENT = "usb_space_fragment";
    public static final String YUN_SPACE_FRAGMENT = "yun_space_fragment";
    public static final String SEARCH_FRAGMENT = "search_fragment";

    public static final String ONLINENEIGHBORFRAGMENT_TAG = "onlineneighborfragment";
    public static final String CLOUDSERVICEFRAGMENT_TAG = "cloudservicefragment";
    public static final String SYSTEMSPACEFRAGMENT_TAG = "systemspacefragment";
    public static final String SYSTEM_SPACE_FRAGMENT_TAG = "System_Space_Fragment_tag";
    public static final String SD_PATH = "/";
    public static final int USB_ONE = 11;
    public static final int USB_TWO = 12;
    public static final int USB_THREE = 13;
    public static final int USB_NOT = 14;
    public static final int USB1_READY = 99;
    public static final int USB2_READY = 98;
    public static final int USB3_READY = 97;
    public static final String PERMISS_DIR_STORAGE_USB = "/storage/usb";
    public static final String KEY_BASE_SD = "key_base_sd";
    public static final String DOUBLE_TAG = "double_tag";
    public static final String PERMISS_DIR_SDCARD = "/sdcard";
    public static final String PERMISS_DIR_STORAGE_SDCARD = "/storage/sdcard";
    public static final String PERMISS_DIR_STORAGE_EMULATED_LEGACY = "/storage/emulated/legacy";
    public static final String PERMISS_DIR_STORAGE_EMULATED_0 = "/storage/emulated/0";
    public static final String PERMISS_DIR_SEAFILE = "/data/sea/data";
    public static final int HEIGHT_MASK = 0x3fffffff;

    private static String TAG = "Constants";
//    public static final String SDCARD_PATH = "/storage/emulated/0";
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String ROOT_PATH = "/";
    public static final String DESKTOP_PATH = SDCARD_PATH + "/Desktop";
    public static final String MUSIC_PATH = SDCARD_PATH + "/Music";
    public static final String VIDEOS_PATH = SDCARD_PATH + "/Movies";
    public static final String PICTURES_PATH = SDCARD_PATH + "/Pictures";
    public static final String DOCUMENT_PATH = SDCARD_PATH + "/documents";
    public static final String DOWNLOAD_PATH = SDCARD_PATH + "/Download";
    public static final String RECYCLE_PATH = SDCARD_PATH + "/Recycle";
    public static final String QQ_IMAGE_PATH = SDCARD_PATH + "/Tencent/QQ_Images";
    public static final String QQ_FILE_PATH = SDCARD_PATH + "/Tencent/QQfile_recv";
    public static final String WEIXIN_IMG_PATH = SDCARD_PATH + "/Tencent/MicroMsg/WeiXin";
    public static final String WEIXIN_FILE_PATH = SDCARD_PATH + "/Tencent/MicroMsg/Download";
    public static final String BAIDU_PAN_PATH = SDCARD_PATH + "/BaiduNetdisk";
//    public static final String USER_PERMISSION_PATH = "/storage";
    public static final String USER_PERMISSION_PATH = "/" + SDCARD_PATH.split("\\/")[1];

    public static final String COMPRESS_FILES = "com.openthos.compress.compress";
    public static final String DECOMPRESS_FILE = "com.openthos.compress.decompress";
    public static final String COMPRESS_PATH_TAG = "paths";
    // Menu id
    public static final int MENU_SHOWHIDE = 117;
    public static final int OPERATION_UP_LEVEL = 3;
    public static final int SEAFILE_DATA_OK = 6;

    public static final int RETURN_TO_WHITE = -2;
    public static final int LIMIT_LENGTH = 10;
    public static final int LIMIT_OWNER_READ = 1;
    public static final int LIMIT_OWNER_WRITE = 2;
    public static final int LIMIT_OWNER_EXECUTE = 3;
    public static final int LIMIT_GROUP_READ = 4;
    public static final int LIMIT_GROUP_WRITE = 5;
    public static final int LIMIT_GROUP_EXECUTE = 6;
    public static final int LIMIT_OTHER_READ = 7;
    public static final int LIMIT_OTHER_WRITE = 8;
    public static final int LIMIT_OTHER_EXECUTE = 9;

    public static final long SIZE_KB = 1024L;
    public static final long SIZE_MB = 1024L*1024L;
    public static final long SIZE_GB = 1024L*1024L*1024L;
    public static final long SIZE_TB = 1024L*1024L*1024L*1024L;

    public static final int INDEX_LIMIT_BEGIN = 14;
    public static final int INDEX_LIMIT_END = 24;
    public static final int INDEX_TIME_BEGIN = 8;
    public static final int INDEX_TIME_END = 27;
    public static final int INDEX_7Z_FILENAME = 53;
    public static final int COMPRESSIBLE = 0;
    public static final int DECOMPRESSIBLE = 1;
    public static final int COMPRESSIBLE_DECOMPRESSIBLE = 2;

    public static final int BAR_Y = 25;

    public static final int USB_CHECKING = 15;
    public static final int USB_MOUNT = 16;
    public static final int USB_UNMOUNT = 17;
    public static final int USB_EJECT = 18;
    public static final int USB_INIT = 19;
    public static final int USB_READY = 20;
    public static final int USB_HIDE = 21;

    public static final String TAG_SYSTEM = "system_default_tag";
    public static final String TAG_USB = "usb_device_tag";
    public static final String TAG_AUTO_MOUNT = "auto_mount_tag";

    public static final long LONG_PRESS_TIME = 500;

    public static final int LIMIT_FILES_NUM = 5;
    public static final int LIMIT_FILES_HEIGHT = 280;

     /**
     *
     * getMIMEType: Get the MIME Types from the file name.
     *
     * @param      file  The name of the file.
     * @return     mimetype the MIME Type of the file.
     * @throws
     */
    public static String getMIMEType(File file) {
        L.i(TAG,"getMIMEType");
        String type= "*/*";

        String name=file.getName();
        int dotIndex = name.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }

        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if(end.equals(""))return type;
        for(int i=0;i<MIME_MapTable.length;i++){
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * The Table of MIME Types
     */
    public static final String[] [] MIME_MapTable = {
            {"3gp", "video/3gpp"},
            {"aab", "application/x-authoware-bin"},
            {"aam", "application/x-authoware-map"},
            {"aas", "application/x-authoware-seg"},
            {"ai", "application/postscript"},
            {"aif", "audio/x-aiff"},
            {"aifc", "audio/x-aiff"},
            {"aiff", "audio/x-aiff"},
            {"als", "audio/X-Alpha5"},
            {"amc", "application/x-mpeg"},
            {"ani", "application/octet-stream"},
            {"apk", "application/vnd.android.package-archive"},
            {"asc", "text/plain"},
            {"asd", "application/astound"},
            {"asf", "video/x-ms-asf"},
            {"asn", "application/astound"},
            {"asp", "application/x-asap"},
            {"asx", "video/x-ms-asf"},
            {"au", "audio/basic"},
            {"avb", "application/octet-stream"},
            {"avi", "video/x-msvideo"},
            {"awb", "audio/amr-wb"},
            {"bcpio", "application/x-bcpio"},
            {"bin", "application/octet-stream"},
            {"bld", "application/bld"},
            {"bld2", "application/bld2"},
            {"bmp", "image/bmp"},
            {"bpk", "application/octet-stream"},
            {"bz2", "application/x-bzip2"},
            {"c", "text/plain"},
            {"cal", "image/x-cals"},
            {"ccn", "application/x-cnc"},
            {"cco", "application/x-cocoa"},
            {"cdf", "application/x-netcdf"},
            {"cgi", "magnus-internal/cgi"},
            {"chat", "application/x-chat"},
            {"class", "application/octet-stream"},
            {"clp", "application/x-msclip"},
            {"cmx", "application/x-cmx"},
            {"co", "application/x-cult3d-object"},
            {"cod", "image/cis-cod"},
            {"conf", "text/plain"},
            {"cpio", "application/x-cpio"},
            {"cpp", "text/plain"},
            {"cpt", "application/mac-compactpro"},
            {"crd", "application/x-mscardfile"},
            {"csh", "application/x-csh"},
            {"csm", "chemical/x-csml"},
            {"csml", "chemical/x-csml"},
            {"css", "text/css"},
            {"cur", "application/octet-stream"},
            {"dcm", "x-lml/x-evm"},
            {"dcr", "application/x-director"},
            {"dcx", "image/x-dcx"},
            {"dhtml", "text/html"},
            {"dir", "application/x-director"},
            {"dll", "application/octet-stream"},
            {"dmg", "application/octet-stream"},
            {"dms", "application/octet-stream"},
            {"doc", "application/msword"},
            {"docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {"dot", "application/x-dot"},
            {"dvi", "application/x-dvi"},
            {"dwf", "drawing/x-dwf"},
            {"dwg", "application/x-autocad"},
            {"dxf", "application/x-autocad"},
            {"dxr", "application/x-director"},
            {"ebk", "application/x-expandedbook"},
            {"emb", "chemical/x-embl-dl-nucleotide"},
            {"embl", "chemical/x-embl-dl-nucleotide"},
            {"eps", "application/postscript"},
            {"eri", "image/x-eri"},
            {"es", "audio/echospeech"},
            {"esl", "audio/echospeech"},
            {"etc", "application/x-earthtime"},
            {"etx", "text/x-setext"},
            {"evm", "x-lml/x-evm"},
            {"evy", "application/x-envoy"},
            {"exe", "application/octet-stream"},
            {"fh4", "image/x-freehand"},
            {"fh5", "image/x-freehand"},
            {"fhc", "image/x-freehand"},
            {"fif", "image/fif"},
            {"fm", "application/x-maker"},
            {"fpx", "image/x-fpx"},
            {"fvi", "video/isivideo"},
            {"gau", "chemical/x-gaussian-input"},
            {"gca", "application/x-gca-compressed"},
            {"gdb", "x-lml/x-gdb"},
            {"gif", "image/gif"},
            {"gps", "application/x-gps"},
            {"gtar", "application/x-gtar"},
            {"gz", "application/x-gzip"},
            {"h", "text/plain"},
            {"hdf", "application/x-hdf"},
            {"hdm", "text/x-hdml"},
            {"hdml", "text/x-hdml"},
            {"hlp", "application/winhlp"},
            {"hqx", "application/mac-binhex40"},
            {"htm", "text/html"},
            {"html", "text/html"},
            {"hts", "text/html"},
            {"ice", "x-conference/x-cooltalk"},
            {"ico", "application/octet-stream"},
            {"ief", "image/ief"},
            {"ifm", "image/gif"},
            {"ifs", "image/ifs"},
            {"imy", "audio/melody"},
            {"ins", "application/x-NET-Install"},
            {"ips", "application/x-ipscript"},
            {"ipx", "application/x-ipix"},
            {"it", "audio/x-mod"},
            {"itz", "audio/x-mod"},
            {"ivr", "i-world/i-vrml"},
            {"j2k", "image/j2k"},
            {"jad", "text/vnd.sun.j2me.app-descriptor"},
            {"jam", "application/x-jam"},
            {"jar", "application/java-archive"},
            {"java", "text/plain"},
            {"jnlp", "application/x-java-jnlp-file"},
            {"jpe", "image/jpeg"},
            {"jpeg", "image/jpeg"},
            {"jpg", "image/jpeg"},
            {"jpz", "image/jpeg"},
            {"js", "application/x-javascript"},
            {"jwc", "application/jwc"},
            {"kjx", "application/x-kjx"},
            {"lak", "x-lml/x-lak"},
            {"latex", "application/x-latex"},
            {"lcc", "application/fastman"},
            {"lcl", "application/x-digitalloca"},
            {"lcr", "application/x-digitalloca"},
            {"lgh", "application/lgh"},
            {"lha", "application/octet-stream"},
            {"lml", "x-lml/x-lml"},
            {"lmlpack", "x-lml/x-lmlpack"},
            {"log", "text/plain"},
            {"lsf", "video/x-ms-asf"},
            {"lsx", "video/x-ms-asf"},
            {"lzh", "application/x-lzh"},
            {"m13", "application/x-msmediaview"},
            {"m14", "application/x-msmediaview"},
            {"m15", "audio/x-mod"},
            {"m3u", "audio/x-mpegurl"},
            {"m3url", "audio/x-mpegurl"},
            {"m4a", "audio/mp4a-latm"},
            {"m4b", "audio/mp4a-latm"},
            {"m4p", "audio/mp4a-latm"},
            {"m4u", "video/vnd.mpegurl"},
            {"m4v", "video/x-m4v"},
            {"ma1", "audio/ma1"},
            {"ma2", "audio/ma2"},
            {"ma3", "audio/ma3"},
            {"ma5", "audio/ma5"},
            {"man", "application/x-troff-man"},
            {"map", "magnus-internal/imagemap"},
            {"mbd", "application/mbedlet"},
            {"mct", "application/x-mascot"},
            {"mdb", "application/x-msaccess"},
            {"mdz", "audio/x-mod"},
            {"me", "application/x-troff-me"},
            {"mel", "text/x-vmel"},
            {"mi", "application/x-mif"},
            {"mid", "audio/midi"},
            {"midi", "audio/midi"},
            {"mif", "application/x-mif"},
            {"mil", "image/x-cals"},
            {"mio", "audio/x-mio"},
            {"mmf", "application/x-skt-lbs"},
            {"mng", "video/x-mng"},
            {"mny", "application/x-msmoney"},
            {"moc", "application/x-mocha"},
            {"mocha", "application/x-mocha"},
            {"mod", "audio/x-mod"},
            {"mof", "application/x-yumekara"},
            {"mol", "chemical/x-mdl-molfile"},
            {"mop", "chemical/x-mopac-input"},
            {"mov", "video/quicktime"},
            {"movie", "video/x-sgi-movie"},
            {"mp2", "audio/x-mpeg"},
            {"mp3", "audio/x-mpeg"},
            {"mp4", "video/mp4"},
            {"mpc", "application/vnd.mpohun.certificate"},
            {"mpe", "video/mpeg"},
            {"mpeg", "video/mpeg"},
            {"mpg", "video/mpeg"},
            {"mpg4", "video/mp4"},
            {"mpga", "audio/mpeg"},
            {"mpn", "application/vnd.mophun.application"},
            {"mpp", "application/vnd.ms-project"},
            {"mps", "application/x-mapserver"},
            {"mrl", "text/x-mrml"},
            {"mrm", "application/x-mrm"},
            {"ms", "application/x-troff-ms"},
            {"msg", "application/vnd.ms-outlook"},
            {"mts", "application/metastream"},
            {"mtx", "application/metastream"},
            {"mtz", "application/metastream"},
            {"mzv", "application/metastream"},
            {"nar", "application/zip"},
            {"nbmp", "image/nbmp"},
            {"nc", "application/x-netcdf"},
            {"ndb", "x-lml/x-ndb"},
            {"ndwn", "application/ndwn"},
            {"nif", "application/x-nif"},
            {"nmz", "application/x-scream"},
            {"nokia-op-logo", "image/vnd.nok-oplogo-color"},
            {"npx", "application/x-netfpx"},
            {"nsnd", "audio/nsnd"},
            {"nva", "application/x-neva1"},
            {"oda", "application/oda"},
            {"ogg", "audio/ogg"},
            {"oom", "application/x-AtlasMate-Plugin"},
            {"pac", "audio/x-pac"},
            {"pae", "audio/x-epac"},
            {"pan", "application/x-pan"},
            {"pbm", "image/x-portable-bitmap"},
            {"pcx", "image/x-pcx"},
            {"pda", "image/x-pda"},
            {"pdb", "chemical/x-pdb"},
            {"pdf", "application/pdf"},
            {"pfr", "application/font-tdpfr"},
            {"pgm", "image/x-portable-graymap"},
            {"pict", "image/x-pict"},
            {"pm", "application/x-perl"},
            {"pmd", "application/x-pmd"},
            {"png", "image/png"},
            {"pnm", "image/x-portable-anymap"},
            {"pnz", "image/png"},
            {"pot", "application/vnd.ms-powerpoint"},
            {"ppm", "image/x-portable-pixmap"},
            {"pps", "application/vnd.ms-powerpoint"},
            {"ppt", "application/vnd.ms-powerpoint"},
            {"pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {"pqf", "application/x-cprplayer"},
            {"pqi", "application/cprplayer"},
            {"prc", "application/x-prc"},
            {"prop", "text/plain"},
            {"proxy", "application/x-ns-proxy-autoconfig"},
            {"ps", "application/postscript"},
            {"ptlk", "application/listenup"},
            {"pub", "application/x-mspublisher"},
            {"pvx", "video/x-pv-pvx"},
            {"qcp", "audio/vnd.qcelp"},
            {"qt", "video/quicktime"},
            {"qti", "image/x-quicktime"},
            {"qtif", "image/x-quicktime"},
            {"r3t", "text/vnd.rn-realtext3d"},
            {"ra", "audio/x-pn-realaudio"},
            {"ram", "audio/x-pn-realaudio"},
            {"rar", "application/ocelet-stream"},
            {"ras", "image/x-cmu-raster"},
            {"rc", "text/plain"},
            {"rdf", "application/rdf+xml"},
            {"rf", "image/vnd.rn-realflash"},
            {"rgb", "image/x-rgb"},
            {"rlf", "application/x-richlink"},
            {"rm", "audio/x-pn-realaudio"},
            {"rmf", "audio/x-rmf"},
            {"rmm", "audio/x-pn-realaudio"},
            {"rmvb", "audio/x-pn-realaudio"},
            {"rnx", "application/vnd.rn-realplayer"},
            {"roff", "application/x-troff"},
            {"rp", "image/vnd.rn-realpix"},
            {"rpm", "audio/x-pn-realaudio-plugin"},
            {"rt", "text/vnd.rn-realtext"},
            {"rte", "x-lml/x-gps"},
            {"rtf", "application/rtf"},
            {"rtg", "application/metastream"},
            {"rtx", "text/richtext"},
            {"rv", "video/vnd.rn-realvideo"},
            {"rwc", "application/x-rogerwilco"},
            {"s3m", "audio/x-mod"},
            {"s3z", "audio/x-mod"},
            {"sca", "application/x-supercard"},
            {"scd", "application/x-msschedule"},
            {"sdf", "application/e-score"},
            {"sea", "application/x-stuffit"},
            {"sgm", "text/x-sgml"},
            {"sgml", "text/x-sgml"},
            {"sh", "application/x-sh"},
            {"shar", "application/x-shar"},
            {"shtml", "magnus-internal/parsed-html"},
            {"shw", "application/presentations"},
            {"si6", "image/si6"},
            {"si7", "image/vnd.stiwap.sis"},
            {"si9", "image/vnd.lgtwap.sis"},
            {"sis", "application/vnd.symbian.install"},
            {"sit", "application/x-stuffit"},
            {"skd", "application/x-Koan"},
            {"skm", "application/x-Koan"},
            {"skp", "application/x-Koan"},
            {"skt", "application/x-Koan"},
            {"slc", "application/x-salsa"},
            {"smd", "audio/x-smd"},
            {"smi", "application/smil"},
            {"smil", "application/smil"},
            {"smp", "application/studiom"},
            {"smz", "audio/x-smd"},
            {"snd", "audio/basic"},
            {"spc", "text/x-speech"},
            {"spl", "application/futuresplash"},
            {"spr", "application/x-sprite"},
            {"sprite", "application/x-sprite"},
            {"spt", "application/x-spt"},
            {"src", "application/x-wais-source"},
            {"stk", "application/hyperstudio"},
            {"stm", "audio/x-mod"},
            {"sv4cpio", "application/x-sv4cpio"},
            {"sv4crc", "application/x-sv4crc"},
            {"svf", "image/vnd"},
            {"svg", "image/svg-xml"},
            {"svh", "image/svh"},
            {"svr", "x-world/x-svr"},
            {"swf", "application/x-shockwave-flash"},
            {"swfl", "application/x-shockwave-flash"},
            {"t", "application/x-troff"},
            {"tad", "application/octet-stream"},
            {"talk", "text/x-speech"},
            {"tar", "application/x-tar"},
            {"taz", "application/x-tar"},
            {"tbp", "application/x-timbuktu"},
            {"tbt", "application/x-timbuktu"},
            {"tcl", "application/x-tcl"},
            {"tex", "application/x-tex"},
            {"texi", "application/x-texinfo"},
            {"texinfo", "application/x-texinfo"},
            {"tgz", "application/x-tar"},
            {"thm", "application/vnd.eri.thm"},
            {"tif", "image/tiff"},
            {"tiff", "image/tiff"},
            {"tki", "application/x-tkined"},
            {"tkined", "application/x-tkined"},
            {"toc", "application/toc"},
            {"toy", "image/toy"},
            {"tr", "application/x-troff"},
            {"trk", "x-lml/x-gps"},
            {"trm", "application/x-msterminal"},
            {"tsi", "audio/tsplayer"},
            {"tsp", "application/dsptype"},
            {"tsv", "text/tab-separated-values"},
            {"tsv", "text/tab-separated-values"},
            {"ttf", "application/octet-stream"},
            {"ttz", "application/t-time"},
            {"txt", "text/plain"},
            {"ult", "audio/x-mod"},
            {"ustar", "application/x-ustar"},
            {"uu", "application/x-uuencode"},
            {"uue", "application/x-uuencode"},
            {"vcd", "application/x-cdlink"},
            {"vcf", "text/x-vcard"},
            {"vdo", "video/vdo"},
            {"vib", "audio/vib"},
            {"viv", "video/vivo"},
            {"vivo", "video/vivo"},
            {"vmd", "application/vocaltec-media-desc"},
            {"vmf", "application/vocaltec-media-file"},
            {"vmi", "application/x-dreamcast-vms-info"},
            {"vms", "application/x-dreamcast-vms"},
            {"vox", "audio/voxware"},
            {"vqe", "audio/x-twinvq-plugin"},
            {"vqf", "audio/x-twinvq"},
            {"vql", "audio/x-twinvq"},
            {"vre", "x-world/x-vream"},
            {"vrml", "x-world/x-vrml"},
            {"vrt", "x-world/x-vrt"},
            {"vrw", "x-world/x-vream"},
            {"vts", "workbook/formulaone"},
            {"wav", "audio/x-wav"},
            {"wax", "audio/x-ms-wax"},
            {"wbmp", "image/vnd.wap.wbmp"},
            {"web", "application/vnd.xara"},
            {"wi", "image/wavelet"},
            {"wis", "application/x-InstallShield"},
            {"wm", "video/x-ms-wm"},
            {"wma", "audio/x-ms-wma"},
            {"wmd", "application/x-ms-wmd"},
            {"wmf", "application/x-msmetafile"},
            {"wml", "text/vnd.wap.wml"},
            {"wmlc", "application/vnd.wap.wmlc"},
            {"wmls", "text/vnd.wap.wmlscript"},
            {"wmlsc", "application/vnd.wap.wmlscriptc"},
            {"wmlscript", "text/vnd.wap.wmlscript"},
            {"wmv", "audio/x-ms-wmv"},
            {"wmx", "video/x-ms-wmx"},
            {"wmz", "application/x-ms-wmz"},
            {"wpng", "image/x-up-wpng"},
            {"wps", "application/vnd.ms-works"},
            {"wpt", "x-lml/x-gps"},
            {"wri", "application/x-mswrite"},
            {"wrl", "x-world/x-vrml"},
            {"wrz", "x-world/x-vrml"},
            {"ws", "text/vnd.wap.wmlscript"},
            {"wsc", "application/vnd.wap.wmlscriptc"},
            {"wv", "video/wavelet"},
            {"wvx", "video/x-ms-wvx"},
            {"wxl", "application/x-wxl"},
            {"x-gzip", "application/x-gzip"},
            {"xar", "application/vnd.xara"},
            {"xbm", "image/x-xbitmap"},
            {"xdm", "application/x-xdma"},
            {"xdma", "application/x-xdma"},
            {"xdw", "application/vnd.fujixerox.docuworks"},
            {"xht", "application/xhtml+xml"},
            {"xhtm", "application/xhtml+xml"},
            {"xhtml", "application/xhtml+xml"},
            {"xla", "application/vnd.ms-excel"},
            {"xlc", "application/vnd.ms-excel"},
            {"xll", "application/x-excel"},
            {"xlm", "application/vnd.ms-excel"},
            {"xls", "application/vnd.ms-excel"},
            {"xlt", "application/vnd.ms-excel"},
            {"xlw", "application/vnd.ms-excel"},
            {"xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {"xm", "audio/x-mod"},
            {"xml", "text/xml"},
            {"xmz", "audio/x-mod"},
            {"xpi", "application/x-xpinstall"},
            {"xpm", "image/x-xpixmap"},
            {"xsit", "text/xml"},
            {"xsl", "text/xml"},
            {"xul", "text/xul"},
            {"xwd", "image/x-xwindowdump"},
            {"xyz", "chemical/x-pdb"},
            {"yz1", "application/x-yz1"},
            {"z", "application/x-compress"},
            {"zac", "application/x-zaurus-zac"},
            {"zip", "application/zip"},
            {"7z", "application/x-7z-compressed"},
            {"", "*/*"}
    };

    public static final String[] compressType = {".zip", ".gz", ".bz2"};

    public static final int COPY = 0x1001;
    public static final int CUT = 0x1002;
    public static final int PASTE = 0x1003;
    public static final int REFRESH = 0x1004;
    public static final int COPY_INFO_SHOW = 0x1005;
    public static final int COPY_INFO_HIDE = 0x1006;
    public static final int COPY_INFO = 0x1007;
    public static final int ONLY_REFRESH = 0x1008;
    public static final int DESKTOP_SHOW_FILE = 0x1009;
    public static final int DESKTOP_DELETE_FILE = 0x1010;

    /*public static final int REFRESH_PERSONAL = 0x1011;
    public static final int DELETE_INFO_SHOW = 0x1012;
    public static final int COMPRESS_INFO_SHOW = 0x1013;
    public static final int DECOMPRESS_INFO_SHOW = 0x1014;
    public static final int SET_CLIPBOARD_TEXT = 0x1015;
    public static final int REFRESH_BY_OBSERVER = 0x1016;*/

    public static final String SUFFIX_TAR = ".tar";
    public static final String SUFFIX_ZIP = ".zip";
    public static final String SUFFIX_RAR = ".rar";
    public static final String SUFFIX_TAR_GZIP = ".gz";
    public static final String SUFFIX_TAR_BZIP2 = ".bz2";
    public static final String SUFFIX_7z = ".7z";

    /*public static final String PATH_TAG = "path";
    public static final String EXTRA_CROP_FILE_HEADER = "OtoCropFile:///";
    public static final String EXTRA_FILE_HEADER = "OtoFile:///";
    public static final String PACKAGENAME_TAG = "package";
    public static final String APPNAME_OTO_LAUNCHER = "com.android.launcher3";
    public static final String EXTRA_DELETE_FILE_HEADER = "OtoDeleteFile:///";*/
}
