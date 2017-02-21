package com.example.simplegame;

import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;


public class MainActivity extends SimpleBaseGameActivity {

	private final int DEFAULT_WIDTH = 800;
	private final int DEFAULT_HEIGHT = 480;
	private Camera mCamera;
	
	//character
	private BitmapTextureAtlas charBitmapTextureAtlas;
	private ITextureRegion charITextureRegion;
	private Sprite charSprite;
	
	//peluru
	private BitmapTextureAtlas peluruBitmapTextureAtlas;
	private ITextureRegion peluruITextureRegion;
	private ArrayList<Sprite> peluruArrayList;
	
	//musuh
	private BitmapTextureAtlas musuhBitmapTextureAtlas;
	private ITextureRegion musuhITextureRegion;
	private ArrayList<Sprite> musuhArrayList;
	
	Boolean sudahkalah = false;
	Boolean sudahremove = false;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0,
		DEFAULT_WIDTH, DEFAULT_HEIGHT);
		EngineOptions mEngineOptions =
		new EngineOptions(true,
		ScreenOrientation.LANDSCAPE_FIXED,
		new RatioResolutionPolicy(DEFAULT_WIDTH,
		DEFAULT_HEIGHT), mCamera);
		return mEngineOptions;
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.
		setAssetBasePath("gfx/");
		
		//character
		charBitmapTextureAtlas =
		new BitmapTextureAtlas(getTextureManager(),180, 90);
		charITextureRegion =
		BitmapTextureAtlasTextureRegionFactory.
		createFromAsset(charBitmapTextureAtlas,
		this, "pesawat.png", 0 , 0);
		charBitmapTextureAtlas.load();
		
		//musuh
		musuhBitmapTextureAtlas =
		new BitmapTextureAtlas(getTextureManager(),60, 40);
		musuhITextureRegion =
		BitmapTextureAtlasTextureRegionFactory.
		createFromAsset(musuhBitmapTextureAtlas,
		this, "musuh.jpg", 0, 0);
		musuhBitmapTextureAtlas.load();
		
		//peluru
		peluruBitmapTextureAtlas =
		new BitmapTextureAtlas(getTextureManager(),20,7);
		peluruITextureRegion=
		BitmapTextureAtlasTextureRegionFactory.
		createFromAsset(peluruBitmapTextureAtlas,
		this, "peluru.png",0,0);
		peluruBitmapTextureAtlas.load();
	}

	@Override
	protected Scene onCreateScene() {
		final Scene mScene = new Scene();
		mScene.setBackground(new Background(255, 255, 255));
		//character
		charSprite = new Sprite(25,
		DEFAULT_HEIGHT/2-charITextureRegion.getHeight()/2,
		charITextureRegion,
		getVertexBufferObjectManager());
		mScene.attachChild(charSprite);
		
		//peluru
		peluruArrayList = new ArrayList<Sprite>();
		mScene.registerUpdateHandler(
		new TimerHandler(0.1f, true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				Sprite newPeluru =
				new Sprite(charSprite.getX()
				+charSprite.getWidth(),
				charSprite.getY()+charSprite.getHeight()/2
				-peluruITextureRegion.getHeight()/2,
				peluruITextureRegion,
				getVertexBufferObjectManager());
				mScene.attachChild(newPeluru);
				peluruArrayList.add(newPeluru);
			}
		}));
		
		//musuh
		musuhArrayList = new ArrayList<Sprite>();
		mScene.registerUpdateHandler(
		new TimerHandler(1.0f, true, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				Random r = new Random();
				int ran = r.nextInt(DEFAULT_HEIGHT-musuhBitmapTextureAtlas.getHeight());
				Sprite newMusuh =
				new Sprite(DEFAULT_WIDTH,
				ran,
				musuhITextureRegion,
				getVertexBufferObjectManager());
				mScene.attachChild(newMusuh);
				musuhArrayList.add(newMusuh);
			}
		}));
		
		mScene.setOnSceneTouchListener
		(new IOnSceneTouchListener() {

			@Override
			public boolean onSceneTouchEvent(Scene pScene,
					TouchEvent pSceneTouchEvent) {
				charSprite.setX(pSceneTouchEvent.getX()
						-charSprite.getWidth()/2);
						charSprite.setY(pSceneTouchEvent.getY()
						-charSprite.getHeight()/2);
						return true;
			}
		});
		
		mScene.registerUpdateHandler(
		new TimerHandler(0.1f, true,
		new ITimerCallback() {
			
			@Override
			public void onTimePassed
			(TimerHandler pTimerHandler) {
				if(!sudahkalah){
				for(int i = 0;
				i < peluruArrayList.size();
				i++) {
						peluruArrayList.get(i).setX(
						peluruArrayList.get(i).getX()+10);
						
					for(int j = 0;
					j< musuhArrayList.size();
					j++){
						musuhArrayList.get(j).setX(
						musuhArrayList.get(j).getX()-1);
						if(peluruArrayList.get(i)
						.collidesWith(musuhArrayList.get(j))){
							//remove
							mScene.detachChild(
							peluruArrayList.get(i));
							peluruArrayList.set(i, null);
							peluruArrayList.remove(i);
							i--;
							mScene.detachChild(
							musuhArrayList.get(j));
							musuhArrayList.set(j, null);
							musuhArrayList.remove(j);
							j--;
							sudahremove = true;
						}else if(musuhArrayList.get(j).getX()<=0){//keluar layar
							sudahkalah = true;
						}else if(musuhArrayList.get(j).collidesWith(
								charSprite)){
							sudahkalah = true;
						}
					}
					//cek keluar layar
					if(!sudahremove){
					if(peluruArrayList.get(i).getX()
					>=DEFAULT_WIDTH){//keluar layar
						//remove
						mScene.detachChild(
						peluruArrayList.get(i));
						peluruArrayList.set(i, null);
						peluruArrayList.remove(i);
						i--;
					}
					}
			}
			}else{
				toastOnUIThread("GAME OVER!!!");
			}
			}
		}));
		return mScene;
	}

}
