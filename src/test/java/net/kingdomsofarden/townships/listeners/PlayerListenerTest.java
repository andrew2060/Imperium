package net.kingdomsofarden.townships.listeners;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.characters.CitizenManager;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.RegionManager;
import net.kingdomsofarden.townships.characters.TownshipsCitizen;
import net.kingdomsofarden.townships.regions.TownshipsRegion;
import net.kingdomsofarden.townships.regions.TownshipsRegionManager;
import net.kingdomsofarden.townships.util.AxisAlignedBoundingBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Random;
import java.util.UUID;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;



@RunWith(PowerMockRunner.class)
@PrepareForTest(PlayerMoveEvent.class)
public class PlayerListenerTest {
    @Test
    public void testOnPlayerMove() {
        System.out.println("Beginning Player Movement Listener Updates Test");
        // Set up mocking
        TownshipsPlugin mockPlugin = mock(TownshipsPlugin.class);
        Player mockPlayer = mock(Player.class);
        UUID pId = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(pId);
        RegionManager rMan = new TownshipsRegionManager(mockPlugin);
        when(mockPlugin.getRegions()).thenReturn(rMan);
        CitizenManager cMan = mock(CitizenManager.class);
        when(mockPlugin.getCitizens()).thenReturn(cMan);
        Citizen citizen = new TownshipsCitizen();
        when(cMan.getCitizen(Mockito.<UUID>any())).thenReturn(citizen);
        PlayerListener listener = new PlayerListener(mockPlugin);
        World mockWorld = mock(World.class);
        UUID wId = UUID.randomUUID();
        when(mockWorld.getUID()).thenReturn(wId);
        // Generate Test Data
        Random rand = new Random();
        for (int x = -9; x <= 9; x++) {
            for (int z = -9; z <= 9; z++) {
                int regions = rand.nextInt(10) + 1;
                for (int i = 0; i < regions; i++) {
                    Region r = mock(TownshipsRegion.class);
                    Location genCenter = new Location(mockWorld, x * 100 + rand.nextInt(180) - 89, 0, z * 100 + rand.nextInt(180) - 89);
                    when(r.getLocation()).thenReturn(genCenter);
                    AxisAlignedBoundingBox bounds = new AxisAlignedBoundingBox(r, rand.nextInt(10) + 1, 5, rand.nextInt(10) + 1);
                    when(r.getBounds()).thenReturn(bounds);
                    when(r.getName()).thenReturn(Optional.<String>absent());
                    when(r.getUid()).thenReturn(UUID.randomUUID());
                    rMan.add(r);
                }
            }
        }
        // Actually do the testing
        for (int multX = -9; multX < 10; multX++) {
            for (int multZ = -9; multZ < 10; multZ++) {
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        Location dest = new Location(mockWorld, 100 * x * multX, 0, 100 * z * multZ);
                        for (int xMod = -1; x < 2; x++) {
                            for (int zMod = -1; z < 2; z++) {
                                Location from = dest.clone().add(xMod, 0, zMod);
                                PlayerMoveEvent mockEvent = mock(PlayerMoveEvent.class);
                                when(mockEvent.getPlayer()).thenReturn(mockPlayer);
                                when(mockEvent.getFrom()).thenReturn(from);
                                when(mockEvent.getTo()).thenReturn(dest);
                                Area correctTo = rMan.getBoundingArea(dest).orNull();
                                listener.onPlayerMove(mockEvent);
                                Area generatedTo = citizen.getCurrentArea();
                                assertMatching(correctTo, generatedTo);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Player Movement Assertion Test Successful");
    }

    private String getCoords(Location dest) {
        return "(" + dest.getX() + ',' + dest.getZ() + ')';
    }

    private void assertMatching(Area correct, Area generated) {
        assert (correct == null && generated == null) || (correct != null && generated != null) :
                "Expected: " + (correct == null ? "null Actual: " + printBounds(generated.getBounds())
                        : printBounds(correct.getBounds()) + " Actual: null");

        if (correct != null) {
            for (int i = 0; i < 4; i++) {
                assert correct.getBounds()[i] == generated.getBounds()[i] : "Mismatch for bound "
                        + i + ": Expected: " + correct.getBounds()[i] + " Actual: " + generated.getBounds()[i];
            }
        }
    }

    private String printBounds(int[] bounds) {
        return new StringBuilder().append(bounds[0]).append(',').append(bounds[1]).append(',').append(bounds[2]).append(',').append(bounds[3]).toString();
    }

}
