package mrriegel.dingding;

import io.netty.buffer.ByteBuf;

import java.awt.Color;

import mrriegel.dingding.ClientProxy.Area;
import mrriegel.dingding.ClientProxy.TextElement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class NotifyMessage implements IMessage {

	int sound = 0;
	String show;
	Area area;
	double color;

	public NotifyMessage() {
	}

	public NotifyMessage(int sound, String show, Area area, double color) {
		super();
		this.sound = sound;
		this.show = show;
		this.area = area;
		this.color = color;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.sound = buf.readInt();
		this.color = buf.readDouble();
		this.show = ByteBufUtils.readUTF8String(buf);
		this.area = Area.valueOf(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.sound);
		buf.writeDouble(this.color);
		ByteBufUtils.writeUTF8String(buf, this.show);
		ByteBufUtils.writeUTF8String(buf, this.area.toString());
	}

	public static class Handler implements IMessageHandler<NotifyMessage, IMessage> {

		@Override
		public IMessage onMessage(final NotifyMessage message, final MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					if (!message.show.isEmpty()) {
						DingDing.proxy.addMessage(new TextElement(message.show, message.area, Color.getHSBColor((float) message.color, 1f, 1f)));
						if (ConfigHandler.inChat)
							Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString(message.show));
					}
					DingDing.proxy.playSound(message.sound);
				}
			});
			return null;
		}

	}

}
