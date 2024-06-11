// noinspection ES6ConvertVarToLetConst

var ASM = Java.type('net.neoforged.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

// noinspection JSUnusedGlobalSymbols
function initializeCoreMod() {
    ASM.loadFile('META-INF/asm/util/util.js');
    return {
        'visibility': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.entity.LivingEntity',
                'methodName': 'getVisibilityPercent',
                'methodDesc': '(Lnet/minecraft/world/entity/Entity;)D'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                instructions.insertBefore(
                    findFirstVarInstruction(methodNode, Opcodes.FSTORE, 4),
                    ASM.listOf(
                        new VarInsnNode(Opcodes.ALOAD, 0),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            'twilightforest/ASMHooks',
                            'modifyClothVisibility',
                            '(FLnet/minecraft/world/entity/LivingEntity;)F',
                            false
                        )
                    )
                );
                return methodNode;
            }
        }
    }
}
