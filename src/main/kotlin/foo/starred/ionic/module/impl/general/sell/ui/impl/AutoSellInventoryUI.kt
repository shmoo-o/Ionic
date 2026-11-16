package foo.starred.ionic.module.impl.general.sell.ui.impl

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.cascade.constraints.base.IPositionConstraint
import foo.starred.cascade.constraints.base.ISizeConstraint
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.MixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.PercentSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.graphics.geometry.CascadeGeometricResolution
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ItemPrimitive.Companion.item
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.screen.CascadeScreen
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.ionic.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.ionic.module.impl.general.sell.impl.AutoSell
import foo.starred.ionic.module.impl.general.sell.ui.data.AutoSellInventorySlotData
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.extension.string.stripped
import foo.starred.kontext.api.text.parser.impl.parse
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

object AutoSellInventoryUI : CascadeScreen("Auto Sell [Ionic]", CascadeGeometricResolution.FHD.of(2f)) {
    private val slots = mutableListOf<AutoSellInventorySlotData>()

    private val text = text {
        wrapper = CascadeTextWrapper
        textSize = 8.5f
        color = CascadeGeometricColor(MochaColorScheme.Text.argb)
        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.START)
        offset = CascadeGeometricOffset(4f, 4f)
    }

    private val tooltip = object : RoundedRectanglePrimitive() {
        override fun draw(graphics: GuiGraphicsExtractor) {
            graphics.nextStratum()
            super.draw(graphics)
        }
    }.apply {
        color = CascadeGeometricColor(MochaColorScheme.Base.argb)
        radius = CascadeGeometricRadius(4f)
        visible = false
        interact = false

        effect(OutlineEffect {
            color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            inset = false
        })

        size = object : ISizeConstraint {
            override fun _width(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = text.wrapper.width(text.text0, text.textSize) + 8f
            override fun _height(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = text.wrapper.height(text.textSize) + 8f
        }

        adopt(text)
    }

    init {
        val window = roundedRectangle {
            position = CenterPositionConstraint()
            size = FixedSizeConstraint(224f, 102f)
            radius = CascadeGeometricRadius(5f)
            color = CascadeGeometricColor(MochaColorScheme.Base.argb)

            effect(OutlineEffect {
                color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                inset = false
            })

            attach(scene)
        }

        val rows = listOf(9..17, 18..26, 27..35, 0..8)
        for ((i0, row) in rows.withIndex()) {
            val y = if (i0 < 3) i0 * 25 else 78

            for ((i1, i2) in row.withIndex()) {
                val x = i1 * 25

                val box = roundedRectangle {
                    size = FixedSizeConstraint(24f, 24f)
                    position = FixedPositionConstraint(x, y)
                    color = CascadeGeometricColor(0)

                    radius = when (i0) {
                        0 if i1 == 0 -> CascadeGeometricRadius(4f, 0f, 0f, 0f)
                        0 if i1 == 8 -> CascadeGeometricRadius(0f, 4f, 0f, 0f)
                        3 if i1 == 0 -> CascadeGeometricRadius(0f, 0f, 4f, 0f)
                        3 if i1 == 8 -> CascadeGeometricRadius(0f, 0f, 0f, 4f)
                        else -> CascadeGeometricRadius.ZERO
                    }

                    attach(window)
                }

                val icon = item {
                    position = CenterPositionConstraint()
                    attach(box)
                }

                val entry = AutoSellInventorySlotData(i2, box, icon).also { slots.add(it) }
                box.on<MouseEvent.Press> {
                    val stack = stack(i2) ?: return@on
                    val name = stack.hoverName.stripped()
                    if (name.isBlank()) return@on

                    val left = button == InputConstants.MOUSE_BUTTON_LEFT
                    if (!left && button != InputConstants.MOUSE_BUTTON_RIGHT) return@on

                    cancel()

                    val list = if (left) AutoSell.allowed else AutoSell.blocked
                    val other = if (left) AutoSell.blocked else AutoSell.allowed

                    if (name in list.value) {
                        list.update { remove(name) }
                        refresh()
                        show(hint(stack), this.x, this.y)
                        return@on
                    }

                    list.update { add(name) }
                    other.update { remove(name) }
                    refresh()
                    show(hint(stack), this.x, this.y)
                }

                box.on<MouseEvent.Move.Enter> {
                    show(hint(stack(i2) ?: return@on), this.x, this.y)
                    entry.update(true)
                }

                box.on<MouseEvent.Move.Any> {
                    if (!tooltip.visible) return@on

                    tooltip(this.x, this.y)
                }

                box.on<MouseEvent.Move.Exit> {
                    entry.update(false)
                    hide()
                }
            }
        }

        for (x in 1..8) {
            rectangle {
                size = FixedSizeConstraint(1f, 102f)
                position = FixedPositionConstraint(x * 25 - 1, 0)
                color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                attach(window)
            }
        }

        rectangle {
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 1))
            position = FixedPositionConstraint(0, 24)
            color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
            attach(window)
        }

        rectangle {
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 1))
            position = FixedPositionConstraint(0, 49)
            color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
            attach(window)
        }

        rectangle {
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 4))
            position = FixedPositionConstraint(0, 74)
            color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
            attach(window)
        }

        tooltip.attach(scene)
        refresh()
    }

    override fun init() {
        super.init()
        refresh()
    }

    private fun tooltip(x: Number, y: Number) {
        tooltip.position = object : IPositionConstraint {
            override fun _x(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float {
                val i0 = x.toFloat() + 8f
                val i1 = element.width
                return if (i0 + i1 > scene.width) (scene.width - i1 - 4f).coerceAtLeast(0f) else i0
            }

            override fun _y(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float {
                val i0 = y.toFloat() + 8f
                val i1 = element.height
                return if (i0 + i1 > scene.height) (y.toFloat() - i1 - 4f).coerceAtLeast(0f) else i0
            }
        }
    }

    private fun show(content: Component, x: Number, y: Number) {
        text.text = content
        tooltip.visible = true
        tooltip(x, y)
    }

    private fun hide() {
        tooltip.visible = false
    }

    private fun hint(stack: ItemStack): Component {
        val name = stack.hoverName.stripped()
        val allowed = name in AutoSell.allowed.value
        val blocked = name in AutoSell.blocked.value

        return when {
            allowed -> "<white>$name <dark_gray>• <green>Allowed <dark_gray>(L-click: unallow • R-click: block)".parse()
            blocked -> "<white>$name <dark_gray>• <red>Blocked <dark_gray>(R-click: unblock • L-click: allow)".parse()
            else -> "<white>$name <dark_gray>• <gray>Normal <dark_gray>(L-click: allow • R-click: block)".parse()
        }
    }

    private fun stack(index: Int): ItemStack? {
        return client.player?.inventory?.nonEquipmentItems?.getOrNull(index)?.takeIf { !it.isEmpty }
    }

    private fun refresh() {
        val items = client.player?.inventory?.nonEquipmentItems ?: return

        for (slot in slots) {
            val stack = items.getOrNull(slot.index) ?: ItemStack.EMPTY

            slot.icon.item = stack
            slot.update(false)
        }
    }
}
