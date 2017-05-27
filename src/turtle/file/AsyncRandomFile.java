package turtle.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.nio.file.StandardOpenOption.*;

/**
 * AsyncRandomFile.java
 *
 * This is an async-write synchronous-read random file access. It will
 * attempt to asynchronously write to the file to avoid having to waste
 * runtime while waiting for available resources, but at the same time return
 * values synchronously whenever we read from the file.
 *
 * @author Henry Wang
 */
public class AsyncRandomFile
{

    /**
     * WrapperInputStream.java
     *
     * This is a wrapper input-stream used in order to avoid using the
     * unwieldy {@link #read(ByteBuffer, long)} method.
     *
     * @author Henry Wang
     */
    private class WrapperInputStream extends InputStream
    {
        private final ByteBuffer byteBuff = ByteBuffer.allocate(1);
        private long pos;

        /**
         * Creates a new wrapper inputstream.
         * @param pos the position to start reading from.
         */
        public WrapperInputStream(long pos)
        {
            this.pos = pos;
        }

        /**
         * Obtains the number of bytes remaining in this input stream before
         * we get to the end of the file. If the number of bytes left is
         * greater than {@link Integer#MAX_VALUE}, it will clamp at the max
         * value.
         * @return the number of bytes left before the end of the file.
         * @throws IOException if an I/O error occurs.
         */
        @Override
        public int available() throws IOException
        {
            return (int)Math.min(Integer.MAX_VALUE, chann.size() - pos);
        }

        /**
         * Skips a number of bytes. It can be positive, to skip forward in the
         * file, or negative, to skip backwards in the file. If the amount of
         * bytes left in the file is less than the number of bytes to skip,
         * it will only skip to the end of the file. Likewise, if the number
         * of bytes to skip backwards exceeds the beginning of the file, it
         * will clamp the skip bytes to the beginning of the file.
         *
         * @param n the number of bytes to skip forward/ backward.
         * @return the actual number of bytes skipped.
         * @throws IOException if an error occurs while skipping.
         */
        @Override
        public long skip(long n) throws IOException
        {
            long skipped = Math.min(n, chann.size() - pos);
            skipped = Math.max(skipped, -pos);
            pos += skipped;
            return skipped;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException
        {
            return AsyncRandomFile.this.read(ByteBuffer.wrap(b, off, len), pos);
        }

        @Override
        public int read() throws IOException
        {
            byteBuff.reset();
            byteBuff.limit(1);

            int read = AsyncRandomFile.this.read(byteBuff, pos);
            byteBuff.flip();
            if (read == 0 || read == -1)
                return -1;

            pos++;
            return byteBuff.get();
        }
    }

    /**
     * WrapperOutputStream.java
     *
     * This is a wrapper output-stream used in order to avoid using the
     * unwieldy {@link #writeAsync(ByteBuffer, long)} method.
     *
     * @author Henry Wang
     */
    private class WrapperOutputStream extends OutputStream
    {
        private final ByteBuffer byteBuff = ByteBuffer.allocate(1);
        private long pos;

        /**
         * Creates a new wrapper inputstream.
         * @param pos the position to start reading from.
         */
        public WrapperOutputStream(long pos)
        {
            this.pos = pos;
        }

        /**
         * Closes the file and flushes any remaining bytes to the underlying
         * system.
         */
        @Override
        public void close()
        {
            flush();
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            writeAsync(ByteBuffer.wrap(b, off, len), pos);
            pos += len;
        }

        @Override
        public void write(int data) throws IOException
        {
            byteBuff.reset();
            byteBuff.limit(1);
            byteBuff.put((byte)data);
            byteBuff.flip();
            writeAsync(byteBuff, pos);
            pos++;
        }

        /**
         * Flushes all data to the underlying system. This will do nothing as
         * all the writes are done asynchronously. If any writes be written
         * you must use the {@link #waitForWrites()} method.
         */
        @Override
        public void flush()
        {
        }
    }

    private final AsynchronousFileChannel chann;
    private final Queue<Future<Integer>> outstandingWrites;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final FileLock lock;

    /**
     * Creates a new async-random file.
     * @param path the path determining the file to read/write.
     * @throws IOException if this fails to open the file for read-write
     * access and lock the file.
     */
    public AsyncRandomFile(Path path) throws IOException
    {
        chann = AsynchronousFileChannel.open(path, CREATE, READ, WRITE);
        lock = chann.tryLock();
        outstandingWrites = new ConcurrentLinkedQueue<>();
    }

    /**
     * @return true if file is open, false if file is closed.
     */
    public boolean isOpen()
    {
        return chann.isOpen();
    }

    /**
     * Closes this file and flushes any remaining writes.
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException
    {
        chann.close();
    }

    /**
     * @return length of file in bytes
     * @throws IOException if an I/O error occurs
     */
    public long length() throws IOException
    {
        return chann.size();
    }

    /**
     * Truncates the size of this file.
     * @param size the size to truncate to.
     * @throws IOException if an I/O error occurs while setting length
     * @see AsynchronousFileChannel#truncate(long)
     */
    public void truncate(long size) throws IOException
    {
        chann.truncate(size);
    }

    /**
     * Reads from the buffer at the specified position. This forces any
     * outstanding writes to the file to be written first.
     *
     * @param dst the destination buffer to read to.
     * @param position position to read from
     * @return number of bytes written, or -1 if reached end of file.
     * @throws IOException if an I/O error occurs while reading
     */
    public int read(ByteBuffer dst, long position) throws IOException
    {
        waitForWrites();
        Future<Integer> waiting = chann.read(dst, position);
        while (true)
        {
            try
            {
                return waiting.get();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            catch (ExecutionException e)
            {
                if (e.getCause() instanceof IOException)
                    throw (IOException)e.getCause();
                else
                    throw new IOException(e.getCause());
            }
        }
    }

    /**
     * Obtains an input-stream at the specified location. This will return a
     * wrapper stream, and will operate like a
     * {@link java.io.FileInputStream} as if a file started at the specified
     * position. This will also guaranteed that any pending writes will be
     * written first by blocking input.
     *
     * @param position the position to start reading from.
     * @return a input stream wrapper
     */
    public InputStream obtainInputStream(long position)
    {
        return new WrapperInputStream(position);
    }

    /**
     * Obtains an output-stream at the specified location. This will return a
     * wrapper stream, and will operate like a
     * {@link java.io.FileOutputStream} as if a file started at the specified
     * position. This will overwrite the existing bytes at the position or
     * append to the file if it goes beyond the current file boundaries.
     *
     * @param position the position to start writing from.
     * @return a output stream wrapper
     */
    public OutputStream obtainOutputStream(long position)
    {
        return new WrapperOutputStream(position);
    }

    /**
     * Waits for all the outstanding write calls to be written.
     */
    public void waitForWrites()
    {
        while (!outstandingWrites.isEmpty())
        {
            Future<Integer> waiting = outstandingWrites.poll();
            while (!waiting.isDone())
            {
                try
                {
                    waiting.get();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.getCause().printStackTrace();
                }
            }
        }
    }

    /**
     * Writes to the file asynchronously
     * @param src the source buffer to write from.
     * @param position the position to start writing
     */
    public void writeAsync(ByteBuffer src, long position)
    {
        outstandingWrites.add(chann.write(src, position));
    }
}
