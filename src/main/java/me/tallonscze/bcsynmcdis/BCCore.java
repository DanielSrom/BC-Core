package me.tallonscze.bcsynmcdis;

import com.mojang.logging.LogUtils;
import me.tallonscze.bcsynmcdis.Command.Commands;
import me.tallonscze.bcsynmcdis.NameFormat.NameFormatEvent;
import me.tallonscze.bcsynmcdis.SyncRank.SynRanks;
import me.tallonscze.bcsynmcdis.Vote.VoteEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BCCore.MODID)
public class BCCore {

    public static final String MODID = "bcsynmcdis";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static VoteEvent voteEvent;
    public static ChromCode chrom;


    public BCCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        chrom = new ChromCode("127.0.0.1", 3306, "bc", "afterlife", "Z8bF77NL9I5A");
        voteEvent = new VoteEvent("127.0.0.1", 3306, "bc", "afterlife", "Z8bF77NL9I5A");

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ModEvent());
        MinecraftForge.EVENT_BUS.register(new Commands());
        MinecraftForge.EVENT_BUS.register(new NameFormatEvent());
        MinecraftForge.EVENT_BUS.register(new SynRanks());
    }

}
