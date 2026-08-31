package dev.diberv.gwCore.mixin;

import net.minecraft.server.packs.FilePackResources;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FilePackResources.class)
public class FilePackResourcesMixin {

    //тут короче нужно перехватить инит и подменить 2 аргумент + проверка на ключ расшифровки
}
