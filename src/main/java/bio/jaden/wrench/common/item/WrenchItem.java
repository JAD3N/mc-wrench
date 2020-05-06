package bio.jaden.wrench.common.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallSignBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        IWorld world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        BlockState newState = null;

        if(isRotationAllowed(state)) {
            // try rotate direction
            if(newState == null) {
                newState = rotateDirection(world, pos, state);
            }

            // try rotate axis
            if(newState == null) {
                newState = rotateAxis(world, pos, state);
            }

            // try rotate rotation
            if(newState == null) {
                newState = rotateRotation(world, pos, state);
            }

            if(newState != null) {
                // fixes stairs and other blocks
                newState = updatePostPlacement(world, pos, newState);

                PlayerEntity player = context.getPlayer();
                SoundType soundType = state.getSoundType(world, pos, player);

                world.setBlockState(pos, newState, 11);
                world.playSound(player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.4f + 0.8f);

                if(player != null) {
                    // deal damage to item
                    context.getItem().damageItem(1, player, (player2) -> {
                        player2.sendBreakAnimation(context.getHand());
                    });
                }

                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.FAIL;
    }

    protected static boolean isRotationAllowed(BlockState state) {
        Block block = state.getBlock();

        if(block instanceof BedBlock
        || block instanceof PistonHeadBlock) {
            return false;
        }

        // check block is not extended (e.g. pistons)
        if(state.has(BlockStateProperties.EXTENDED) && state.get(BlockStateProperties.EXTENDED)) {
            return false;
        }

        // check block is not a part of a chest
        if(state.has(BlockStateProperties.CHEST_TYPE) && state.get(BlockStateProperties.CHEST_TYPE) != ChestType.SINGLE) {
            return false;
        }

        return true;
    }

    protected static BlockState updatePostPlacement(IWorld world, BlockPos pos, BlockState state) {
        DirectionProperty directionProperty = getDirectionProperty(state);

        // check facing property
        if(directionProperty != null) {
            Direction facing = state.get(directionProperty);

            if(facing != null) {
                BlockPos facingPos = pos.offset(facing);
                BlockState facingState = world.getBlockState(facingPos);

                state = state.updatePostPlacement(facing, facingState, world, pos, facingPos);
            }
        }

        return state;
    }

    protected static <T extends Comparable<T>> BlockState rotateProperty(BlockState state, IProperty<T> property, Predicate<T> filter) {
        if(!state.has(property)) {
            return null;
        }

        T currentValue = state.get(property);
        List<T> array = new ArrayList<>(property.getAllowedValues());

        for(int i = array.size() - 1; i >= 0; i--) {
            T value = array.get(i);

            // skip checking existing value
            if(value == currentValue) {
                continue;
            }

            // check if value is applicable
            if(filter != null && filter.test(value)) {
                array.remove(value);
            }
        }

        // cannot rotate array of 1
        if(array.size() <= 1) {
            return null;
        }

        int index = array.indexOf(currentValue);
        index = (index + 1) % array.size();

        T newValue = array.get(index);
        BlockState newState = state.with(property, newValue);

        return newState;
    }

    protected static BlockState rotateDirection(IWorld world, BlockPos pos, BlockState state) {
        DirectionProperty directionProperty = getDirectionProperty(state);

        // check facing property
        if(directionProperty == null) {
            return null;
        }

        Block block = state.getBlock();
        Direction direction = state.get(directionProperty);
        Direction[] directions = block.getValidRotations(state, world, pos);

        // check directions is valid
        if(directions == null) {
            directions = directionProperty.getAllowedValues().toArray(new Direction[0]);
        }


        return rotateProperty(state, directionProperty, (dir) -> {
            if(dir == direction) {
                return false;
            }

            BlockState tmpState = state.with(directionProperty, dir);
            boolean isValidPos = tmpState.isValidPosition(world, pos);

            BlockState facingState = world.getBlockState(pos.offset(dir, -1));
            Block facingBlock = facingState.getBlock();

            // check that signs are not now attached to eachother
            if(isValidPos && facingBlock instanceof WallSignBlock && block instanceof WallSignBlock) {
                if(facingState.get(directionProperty).getOpposite().equals(dir)) {
                    isValidPos = false;
                }
            }

            return !isValidPos;
        });
    }

    protected static BlockState rotateAxis(IWorld world, BlockPos pos, BlockState state) {
        EnumProperty<Direction.Axis> axisProperty = getAxisProperty(state);

        // check facing property
        if(axisProperty == null) {
            return null;
        }

        return rotateProperty(state, axisProperty, null);
    }

    protected static BlockState rotateRotation(IWorld world, BlockPos pos, BlockState state) {
        return rotateProperty(state, BlockStateProperties.ROTATION_0_15, null);
    }

    protected static DirectionProperty getDirectionProperty(BlockState state) {
        if(state.has(BlockStateProperties.FACING)) {
            return BlockStateProperties.FACING;
        } else if(state.has(BlockStateProperties.FACING_EXCEPT_UP)) {
            return BlockStateProperties.FACING_EXCEPT_UP;
        } else if(state.has(BlockStateProperties.HORIZONTAL_FACING)) {
            return BlockStateProperties.HORIZONTAL_FACING;
        } else {
            return null;
        }
    }

    protected static EnumProperty<Direction.Axis> getAxisProperty(BlockState state) {
        if(state.has(BlockStateProperties.HORIZONTAL_AXIS)) {
            return BlockStateProperties.HORIZONTAL_AXIS;
        } else if(state.has(BlockStateProperties.AXIS)) {
            return BlockStateProperties.AXIS;
        } else {
            return null;
        }
    }
}