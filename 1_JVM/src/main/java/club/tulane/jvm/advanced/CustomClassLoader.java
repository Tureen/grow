package club.tulane.jvm.advanced;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 自定义加载器, 用于加载 xlass 文件
 * xlass 文件特性: 所有字节反转（x=255-x)
 */
public class CustomClassLoader extends ClassLoader {

    private static final String FILETYPE = ".xlass";

    public static Map<String, File> FILEMAP = new ConcurrentHashMap<>();

    private static Map<ClassLoader, Map<String, Class<?>>> CLASSLOADER_CACHE = new ConcurrentHashMap<>();

    private static Map<String, ClassLoader> CLASSNAMEMAP = new ConcurrentHashMap<>();

    /**
     * 解析指定路径下的所有 xlass 到缓存, 用于类名与路径的映射
     *
     * @param url
     */
    public void addURL(String url) throws IOException {
        final File sourceFile = new File(url);
        if (".xar".equals(FileUtils.getFileExtension(sourceFile))) {
            final List<String> urls = loadXar(sourceFile);
            for (String urlTmp : urls) {
                buildCache(urlTmp);
            }
            return;
        }
        buildCache(url);
    }

    /**
     * 将xar包内容读取到临时目录
     *
     * @param sourceFile
     * @return
     * @throws IOException
     */
    private List<String> loadXar(File sourceFile) throws IOException {
        List<String> list = new LinkedList<>();
        try (JarFile jarFile = new JarFile(sourceFile)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry e = entries.nextElement();
                String name = e.getName();
                if (FileUtils.checkExtension(name, FILETYPE)) {
                    // 解析出包含包结构的名称
                    String clzName = name.replaceAll("/|\\$", ".");

                    // 将jar包资源暂时读入临时资源路径
                    byte[] buffer;
                    try (final InputStream in = jarFile.getInputStream(e)) {
                        buffer = new byte[in.available()];
                        in.read(buffer);
                    }
                    final File dir = new File("1_JVM/src/main/resources/xar");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    final String targetPath = "1_JVM/src/main/resources/xar/" + clzName;
                    File targetFile = new File(targetPath);
                    try (OutputStream outStream = new FileOutputStream(targetFile)) {
                        outStream.write(buffer);
                    }
                    list.add(targetPath);
                }
            }
        }
        return list;
    }

    private void buildCache(String url) {
        final List<File> fromDisk = getFromDisk(url);
        final Map<String, File> fileMap = fromDisk.stream().collect(Collectors.toMap(FileUtils::getFileName, x -> x));
        CustomClassLoader.FILEMAP.putAll(fileMap);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // 卸载旧类加载器
        unLoadClassLoader(name);

        // 先检查加载类是否有缓存, 有则直接返回
        final Map<String, Class<?>> classMap = CLASSLOADER_CACHE.computeIfAbsent(this, v -> new ConcurrentHashMap<>());
        Class<?> classFromCache = classMap.get(name);
        if (classFromCache == null) {
            classFromCache = super.loadClass(name);
            // 缓存已加载类
            classMap.put(name, classFromCache);
            CLASSNAMEMAP.put(name, this);
        }
        return classFromCache;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final File file = FILEMAP.get(name);
        if (!file.exists()) {
            throw new ClassNotFoundException("don't find class path !");
        }
        final byte[] fromFile = getFromFile(file);
        final byte[] bytes = reverseBytes(fromFile);
        return defineClass(name, bytes, 0, bytes.length);
    }

    /**
     * 卸载对应类名的类加载器
     * @param name
     */
    public static void unLoadClassLoader(String name){
        final ClassLoader oldClassLoader = CLASSNAMEMAP.get(name);
        if (oldClassLoader != null) {
            unLoadClassLoader(oldClassLoader);
        }
    }

    /**
     * 卸载类加载器
     *
     * @param classLoader
     */
    private static void unLoadClassLoader(ClassLoader classLoader) {
        // 清除标记: 类名与加载器的映射关系
        final Map<String, Class<?>> classMap = CLASSLOADER_CACHE.get(classLoader);
        if (classMap != null) {
            for (String className : classMap.keySet()) {
                CLASSNAMEMAP.remove(className);
            }
        }
        // 清除缓存: 类加载器与Class映射关系
        CLASSLOADER_CACHE.remove(classLoader);
    }

    /**
     * 根据文件解析字节数组
     *
     * @param file
     * @return
     */
    private byte[] getFromFile(File file) {
        byte[] bytes = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            bytes = new byte[fis.available()];
            fis.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 解析: 反转字节
     *
     * @param bytes
     * @return
     */
    private byte[] reverseBytes(byte[] bytes) {
        byte[] rBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            rBytes[i] = (byte) (255 - bytes[i]);
        }
        return rBytes;
    }

    /**
     * 得到文件夹下所有文件
     *
     * @return
     */
    private List<File> getFromDisk(String lib) {
        File file = new File(lib);
        List<File> files = new LinkedList<>();
        if (!file.isDirectory()) {
            if (FileUtils.checkFileExtension(file, FILETYPE)) {
                files.add(file);
            }
            return files;
        }

        String[] filepaths = file.list();
        if (filepaths != null) {
            for (String filepath : filepaths) {
                final String path = lib + java.io.File.separator + filepath;
                File zfile = new File(path);
                if (!zfile.isDirectory()) {
                    files.add(zfile);
                    continue;
                }
                final List<File> zfiles = recursionBuildFilePaths(path, new LinkedList<>());
                files.addAll(zfiles);
            }
        }
        // 过滤除 FILETYPE 以外类型的文件
        return files.stream().filter(f -> FileUtils.checkFileExtension(f, FILETYPE)).collect(Collectors.toList());
    }

    /**
     * 递归文件夹, 得到所有文件集合
     *
     * @param diskUrl
     * @param files
     * @return
     */
    private List<File> recursionBuildFilePaths(String diskUrl, List<File> files) {
        File file = new File(diskUrl);
        if (!file.isDirectory()) {
            files.add(file);
            return files;
        }

        String[] filepaths = file.list();
        if (filepaths != null) {
            for (String filepath : filepaths) {
                final String path = diskUrl + java.io.File.separator + filepath;
                File zfile = new File(path);
                if (file.isDirectory()) {
                    recursionBuildFilePaths(path, files);
                    continue;
                }
                files.add(zfile);
            }
        }
        return files;
    }
}
