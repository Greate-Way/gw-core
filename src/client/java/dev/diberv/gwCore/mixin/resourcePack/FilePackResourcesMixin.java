package dev.diberv.gwCore.mixin.resourcePack;

import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FilePackResources.class)
public class FilePackResourcesMixin {

    //тут короче нужно перехватить инит и подменить 2 аргумент + проверка на ключ расшифровки
    @ModifyVariable(
            method = "<init>",
            at = @At("HEAD"),
            argsOnly = true,
            index = 1
    )
    private static FilePackResources.SharedZipFileAccess modifyzipFileAccess(FilePackResources.SharedZipFileAccess zipFileAccess) {

        return zipFileAccess;
    }
}
