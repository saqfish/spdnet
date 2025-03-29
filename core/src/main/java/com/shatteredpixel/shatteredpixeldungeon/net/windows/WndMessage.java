package com.shatteredpixel.shatteredpixeldungeon.net.windows;


import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;

public class WndMessage extends NetWindow {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 144;

	private static final int MARGIN 		= 2;

	private IconTitle icon;
	private RenderedTextBlock title;
	private RenderedTextBlock message;

	public WndMessage(Image icon, String title, String message) {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		float pos = 0;
		if (title != null) {
			this.icon = new IconTitle(icon, title);
			this.icon.setRect(0, pos, width, 0);
			add(this.icon);

			pos = this.icon.bottom() + 2*MARGIN;
		}

		layoutBody(pos, message);
	}

	public WndMessage(String title, String message) {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		float pos = MARGIN;
		if (title != null) {
			this.title = PixelScene.renderTextBlock(title, 9);
			this.title.hardlight(TITLE_COLOR);
			this.title.setPos(MARGIN, pos);
			this.title.maxWidth(width - MARGIN * 2);
			add(this.title);

			pos = this.title.bottom() + 2*MARGIN;
		}

		layoutBody(pos, message);
	}

	private void layoutBody(float pos, String message){
		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		this.message = PixelScene.renderTextBlock( 6 );
		this.message.text(message, width);
		this.message.setPos( 0, pos );
		add( this.message );

		pos = this.message.bottom() + 2*MARGIN;

		resize( width, (int)(pos - MARGIN) );
	}

	public void setMessage(String message){
		this.message.text(message);
	}

	protected boolean enabled( int index ){
		return true;
	}

	protected void onSelect( int index ) {}
}
