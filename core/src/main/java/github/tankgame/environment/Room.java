package github.tankgame.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import github.tankgame.characters.Monster;
import github.tankgame.characters.boss.Boss;
import github.tankgame.characters.boss.Chub;
import github.tankgame.characters.monsters.Clotty;
import github.tankgame.characters.monsters.Horf; // Example monster type
import github.tankgame.environment.blocks.Rock;
import github.tankgame.environment.blocks.Wall;
import github.tankgame.items.Item;
import github.tankgame.items.Pill;
import github.tankgame.characters.Character;

import java.io.*;
import java.util.Objects;
import java.util.Random;

public class Room {
    private Texture roomTexture;
    private Texture itemPedestalTexture;
    private TextureRegion itemPedestalTextureRegion;
    private String texturePath;
    private boolean[][] collisionMap;
    private float tileWidth, tileHeight;
    private float roomWidth, roomHeight;
    private boolean isItemRoom, isBossRoom;

    private Array<Door> doors = new Array<>();
    private boolean[] roomGrid;
    private int roomIndex;
    private int rowCount, columnCount;

    private float scale;

    private Array<Monster> monsters = new Array<>();
    private Array<Rock> rocks = new Array<>();
    private Array<Wall> walls = new Array<>();

    private Array<Rectangle> solidTiles = new Array<>();

    private Pill pill;
    private boolean pillSpawned = false;

    private Item item;
    private Boss boss;

    public Room(String texturePath, boolean[] roomGrid, int roomIndex, boolean isItemRoom, boolean isBossRoom) {
        roomTexture = new Texture(Gdx.files.internal(texturePath));
        this.roomGrid = roomGrid;
        this.texturePath = texturePath;
        this.roomIndex = roomIndex;
        this.isItemRoom = isItemRoom;
        this.isBossRoom = isBossRoom;

        if (isItemRoom) {
            this.itemPedestalTexture = new Texture(Gdx.files.internal("items/item_pedestal_sheet.png"));
            this.itemPedestalTextureRegion = new TextureRegion(itemPedestalTexture, 0, 0, 32, 32);

            spawnItem();
        }

        if (isBossRoom) {
            spawnBoss();  // Spawn boss if it's a boss room
        }

        roomWidth = roomTexture.getWidth();
        roomHeight = roomTexture.getHeight();

        String formattedPath = texturePath.replace(".png", "_map.csv").replace("rooms/", "rooms/maps/");
        loadRoomFromCSV(formattedPath);  // Changed to loadRoomFromCSV
    }

    private void spawnBoss() {
        float bossX = Gdx.graphics.getWidth()/2;
        float bossY = Gdx.graphics.getHeight()/4;

        // Spawn the boss
        boss = new Chub(bossX, bossY);
        boss.setRoom(this);
    }

    private void spawnItem() {
        if (isItemRoom) {
            Random random = new Random();

            // Randomly choose one of three stats
            String[] stats;
            float value;

            int statChoice = random.nextInt(3);
            switch (statChoice) {
                case 0:
                    stats = new String[]{"health", "damage"};
                    value = 1.0f;
                    break;
                case 1:
                    stats = new String[]{"bomb"};
                    value = 99;
                    break;
                case 2:
                    stats = new String[]{"triple"};
                    value = 3;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + statChoice);
            }

            // Create the item and add it to the room
            item = new Item(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight()/2, stats, value);
        }
    }

    public void checkItemPickup(Character character) {
        if (item != null && item.isPickedUp(character)) {
            item.applyEffect(character); // Apply the effect of the item to the character
            item.dispose(); // Dispose of the item after use
            item = null;
        }
    }

    public void addDoor(Door door) {
        this.doors.add(door);
    }

    public Array<Door> getDoors() {
        return this.doors;
    }

    public Array<Monster> getMonsters() {
        return this.monsters;
    }

    public void render(SpriteBatch batch, float windowWidth, float windowHeight, float scaledWidth, float scaledHeight, float scale) {
        // Calculate offsets to center the room
        float offsetX = (windowWidth - scaledWidth) / 2f;
        float offsetY = (windowHeight - scaledHeight) / 2f;

        // Draw the room with scaling and centering
        batch.draw(
            this.getRoomTexture(),
            offsetX,
            offsetY,
            scaledWidth,
            scaledHeight
        );

        for (Door door : this.doors) {

            if (Objects.equals(0, this.monsters.size)) {
                door.setLocked(false);
            } else {
                door.setLocked(true);
            }

            if(this.boss != null) {
                door.setLocked(true);
            }

            door.render(batch); // Render each door
        }

        for (Rock rock : this.rocks) {
            rock.render(batch);
        }

        for (Wall wall : this.walls) {
            wall.render(batch);
        }

        if (this.isItemRoom) {
            batch.draw(itemPedestalTextureRegion, this.roomWidth / 2 - 20, this.roomHeight / 2 - 40, 50, 50);
        }

        // Render monsters
        if (!Objects.equals(0, this.monsters.size)) {
            for (Monster monster : this.monsters) {
                monster.render(batch, scale);
                if (monster.isDead()) {
                    monster.playDeathAnimation();
                    this.removeDeadMonsters();
                }
            }
        }

        if (pill != null) {
            pill.render(batch);
        }

        if (item != null && isItemRoom) {
            item.render(batch);
        }

        if (this.isBossRoom && boss != null) {
            if(boss.isDead()){
                boss.playDeathAnimation();
                if(boss.isDeathAnimationFinished()){
                    removeBoss();
                }
            }
            if(boss != null){
                boss.render(batch, scale);  // Render the boss in the boss room
            }

        }

    }

    private void removeBoss() {
        if (boss != null) {
            boss.dispose();
            boss = null;    // Remove the boss from the room
        }
    }

    public Boss getBoss(){
        return this.boss;
    }

    public void loadRoomFromCSV(String filePath) {
        FileHandle fileHandle = Gdx.files.internal(filePath);
        if (!fileHandle.exists()) {
            System.err.println("File not found: " + filePath);
            return;
        }

        try {
            BufferedReader br = new BufferedReader(fileHandle.reader());

            String line;
            this.rowCount = 0;
            this.columnCount = 0;

            // First pass to determine the number of rows and columns in the CSV
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                columnCount = Math.max(columnCount, values.length);
                rowCount++;
            }

            // Calculate tile dimensions
            tileWidth = roomWidth / columnCount;
            tileHeight = roomHeight / rowCount;

            solidTiles.clear();

            // Second pass to populate the solidTiles array and spawn monsters
            br = new BufferedReader(fileHandle.reader());
            int row = 0;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int col = 0; col < values.length; col++) {
                    int value = Integer.parseInt(values[col].trim());
                    if (value == 1) {  // Solid tile
                        // Add the rectangle for this solid tile to the solidTiles array
                        solidTiles.add(new Rectangle(col * tileWidth, row * tileHeight, tileWidth, tileHeight));
                    }



                    if (!isItemRoom && !isBossRoom) {

                        if (value == 2) {
                            monsters.add(new Horf(col * tileWidth, row * tileHeight));
                        }

                        if (value == 3) {
                            monsters.add(new Clotty(col * tileWidth, row * tileHeight));
                        }

                        if (value == 4) {
                            rocks.add(new Rock(col * tileWidth + tileWidth/2 - 30 , row * tileHeight + tileHeight/2 - 30));
                            // add rocks and walls to solid tiles then I can check in CollisionDetector.checkCharacterTileCollision
                            //solidTiles.add(new Rectangle(col * tileWidth + tileWidth / 2, row * tileHeight + tileHeight / 2, 50, 50));
                        }

                        if (value == 5) {
                            walls.add(new Wall(col * tileWidth + tileWidth/2 -30, row * tileHeight + tileHeight/2 - 30));
                            //solidTiles.add(new Rectangle(col * tileWidth + tileWidth / 2, row * tileHeight + tileHeight / 2, 50, 50));
                        }
                    }

                }
                row++;
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

        for (Monster monster : monsters) {
            monster.setRoom(this);
        }
    }

    public Array<Rectangle> getSolidTiles() {
        return this.solidTiles;
    }

    public void removeDeadMonsters() {
        for (int i = monsters.size - 1; i >= 0; i--) { // Iterate backwards
            Monster monster = monsters.get(i);
            if (monster.isDead() && monster.isDeathAnimationFinished()) {
                monster.dispose(); // Dispose resources
                this.monsters.removeIndex(i); // Remove monster
            }
        }

        // If no monsters are left and a pill hasn't been spawned yet, spawn a pill
        if (monsters.size == 0 && !pillSpawned) {
            spawnPill();
        }
    }

    private void spawnPill() {
        pillSpawned = true;
        Random random = new Random();
        if (random.nextBoolean()) { // 50/50 chance to spawn a pill
            // Spawn a pill at the center of the room
            float pillX = roomWidth / 2 - 20;
            float pillY = roomHeight / 2 - 20;

            // Randomly choose one of three stats
            String stat;
            float value;

            int statChoice = random.nextInt(3);
            switch (statChoice) {
                case 0:
                    stat = "health";
                    value = 1.0f;
                    break;
                case 1:
                    stat = "speed";
                    value = 0.9f;
                    break;
                case 2:
                    stat = "tears";
                    value = 0.05f;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + statChoice);
            }

            // Create the pill with the selected stat and value
            pill = new Pill(pillX, pillY, stat, value);
        }
    }

    public void checkPillPickup(Character character) {
        if (pill != null && pill.isPickedUp(character)) {
            pill.playPillSound();
            pill.applyEffect(character);
            pill.dispose();
            pill = null; // Remove the pill after pickup
        }
    }

    public Array<Rock> getRocks() {
        return this.rocks;
    }

    public Array<Wall> getWalls() {
        return this.walls;
    }

    public void setItemRoom(boolean itemRoom) {
        this.isItemRoom = itemRoom;
    }

    public boolean isItemRoom() {
        return this.isItemRoom;
    }

    public boolean isBossRoom() {
        return this.isBossRoom;
    }

    public String getTexturePath() {
        return this.texturePath;
    }

    public Texture getRoomTexture() {
        return this.roomTexture;
    }

    public int getRoomIndex() {
        return this.roomIndex;
    }

    public float getRoomWidth() {
        return this.roomWidth;
    }

    public float getRoomHeight() {
        return this.roomHeight;
    }

    public void dispose() {
        roomTexture.dispose();
        for (Door door : this.doors) {
            door.dispose();
        }
        if (pill != null) {
            pill.dispose();
        }
    }
}
