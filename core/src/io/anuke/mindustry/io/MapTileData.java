package io.anuke.mindustry.io;

import com.badlogic.gdx.utils.IntIntMap;
import io.anuke.ucore.util.Bits;
import io.anuke.ucore.util.Log;

import java.nio.ByteBuffer;

public class MapTileData {
    /**Tile size: 3 bytes. <br>
     * 0: ground tile <br>
     * 1: wall tile <br>
     * 2: rotation + team <br>
     * 3: link (x/y) <br>*/
    private final static int TILE_SIZE = 4;

    private final ByteBuffer buffer;
    private final TileDataMarker tile = new TileDataMarker();
    private final int width, height;
    private final boolean readOnly;

    private IntIntMap map;

    public MapTileData(int width, int height){
        this.width = width;
        this.height = height;
        this.map = null;
        this.readOnly = false;
        buffer = ByteBuffer.allocate(width * height * TILE_SIZE);
    }

    public MapTileData(byte[] bytes, int width, int height, IntIntMap mapping, boolean readOnly){
        buffer = ByteBuffer.wrap(bytes);
        this.width = width;
        this.height = height;
        this.map = mapping;
        this.readOnly = readOnly;

        if(mapping != null && !readOnly){
            for(int i = 0; i < width * height; i ++){
                read();
                buffer.position(i * TILE_SIZE);
                write();
            }
            buffer.position(0);
            this.map = null;
        }

        read();
    }

    public byte[] toArray(){
        return buffer.array();
    }

    public int width(){
        return width;
    }

    public int height(){
        return height;
    }

    public TileDataMarker getMarker() {
        return tile;
    }

    /**Reads and returns the next tile data.*/
    public TileDataMarker read(){
        tile.read(buffer);
        return tile;
    }

    /**Reads and returns the next tile data.*/
    public TileDataMarker read(TileDataMarker marker){
        marker.read(buffer);
        return marker;
    }

    /**Reads and returns the next tile data.*/
    public TileDataMarker readAt(int x, int y){
        position(x, y);
        tile.read(buffer);
        return tile;
    }

    /**Reads and returns the next tile data.*/
    public TileDataMarker readAt(int x, int y, TileDataMarker marker){
        position(x, y);
        marker.read(buffer);
        return marker;
    }

    /**Reads and returns a specific byte position.*/
    public byte readAt(int x, int y, int offset){
        buffer.position((x + width * y) * TILE_SIZE + offset);
        return buffer.get();
    }

    /**Writes and returns the next tile data.*/
    public void write(){
        tile.write(buffer);
    }

    /**Writes tile data at a specified position.*/
    public void write(int x, int y, TileDataMarker writer){
        position(x, y);
        writer.write(buffer);
    }

    /**Sets read position to the specified coordinates*/
    public void position(int x, int y){
        buffer.position((x + width * y) * TILE_SIZE);
    }

    public class TileDataMarker {
        public byte floor, wall;
        public byte link;
        public byte rotation;
        public byte team;

        public void read(ByteBuffer buffer){
            floor = buffer.get();
            wall = buffer.get();
            link = buffer.get();
            byte rt = buffer.get();
            rotation = Bits.getLeftByte(rt);
            team = Bits.getRightByte(rt);

            if(map != null){
                floor = (byte)map.get(floor, floor);
                wall = (byte)map.get(wall, wall);
            }
        }

        public void write(ByteBuffer buffer){
            if(readOnly) throw new IllegalArgumentException("This data is read-only.");
            buffer.put(floor);
            buffer.put(wall);
            buffer.put(link);
            buffer.put(Bits.packByte(rotation, team));
        }

        public void writeWallLink(){
            buffer.position(buffer.position() + 1);
            buffer.put(wall);
            buffer.put(link);
        }
    }
}
