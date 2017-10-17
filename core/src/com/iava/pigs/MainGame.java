package com.iava.pigs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class MainGame extends ApplicationAdapter {

	/*Смена флага ведет к выбору анимации: вдох или выдох*/
	private boolean flag;
	/*Определяет взорвался ли персонаж*/
	private boolean checkBah = false;
	/*Определяет задохнулся ли персонаж*/
	private boolean checkDie = false;

	/*Задержка меджу кадарми анимации*/
	final float DELAY = 0.50f;

	/*Константы на сколько разбивать спрайтлист*/
	private static final int FRAME_COLS = 6;
	private static final int FRAME_ROWS = 6;

	/*Анимация создание наборов кадров с величиной задержки после отрисовки каждого*/
	private Animation walkAnimation;

	/*переменная для текстуры*/
	private Texture walkSheet;

	/*Хранит фрагмет текстуры, здесь массив таких фрагментов*/
	private TextureRegion[] walkFrames;

	/*Класс для визуализации*/
	private SpriteBatch spriteBatch;

	/*Фрагмент текстуры который необходимо отризовать в данный момент*/
	private TextureRegion currentFrame;

	/*Время прошедшее с начала визуализации отдельно для анимации дыхания и ветра,
	 зная задержки в анимации можно расчитать текущий кадр*/
	private float stateTime, blowTime;

	/*Массив кадров вырезанных из спрайт листа, отдельно для вдоха и выдоха*/
	TextureRegion[] expirationFrames;
	TextureRegion[] inspirationFrames;
	TextureRegion[] blowFrames;
	TextureRegion[] bahFrames;
	TextureRegion[] deathFrames;

	/*Анимация из вышеописанных массивов, только теперь с прописаной задержкой между этими кадрами*/
	Animation expirationAnimation;
	Animation inspirationAnimation;
	Animation blowAnimation;
	Animation bahAnimation;
	Animation deathAnimation;

	Sound soundInsp, soundExp, soundBah, soundCough;




	@Override
	public void create() {

		/*Устанавливаем флаг в истину, что означает что при запуске игры визуализация начнется с выдоха*/
		flag = true;

		/*Загружаем спрайтлист*/
		walkSheet = new Texture(Gdx.files.internal("spritlist2.png"));

		/*Делим его на части (области, кадры) шириной 32х32 пикселя, так как область нашего персонажа равна 32 на 32*/
		TextureRegion[][] tmp = TextureRegion.split(walkSheet, 32, 32);

		/*Создаем соответствующие массивы для вдоха и для выдоха, по 6 элементов в каждом, так как в каждой из этих анимаций по 6 кадов*/
		expirationFrames = new TextureRegion[FRAME_COLS];
		inspirationFrames = new TextureRegion[FRAME_COLS];
		blowFrames = new  TextureRegion[FRAME_COLS];
		bahFrames = new TextureRegion[4];
		deathFrames = new TextureRegion[4];

		/*Заполняем эти массивы циклом*/

		int ind = 0;
		for (int j = 0; j < FRAME_COLS; j++, ind++) {
			/*Вдох у нас в первой строчке спрайтлиста, выдох во второй, далее увеличивая значение
			* j берем последовательно кадры этих строчек и запихиваем их в соответствующие массивы*/
			expirationFrames[ind] = tmp[0][j];
			inspirationFrames[ind] = tmp[1][j];
			blowFrames[ind] = tmp[3][j];

			if(j>3){continue;}
			bahFrames[ind] = tmp[4][j];
			deathFrames[ind] = tmp[5][j];
		}


		/*Делаем из массивов анимацию, устанавливая интревал между кадрами хранимыми в них*/
		expirationAnimation = new Animation(DELAY, expirationFrames);
		inspirationAnimation = new Animation(DELAY, inspirationFrames);
		blowAnimation = new Animation(DELAY, blowFrames);
		bahAnimation = new Animation(0.07f, bahFrames);
		deathAnimation = new Animation(DELAY, deathFrames);


		soundInsp = Gdx.audio.newSound(Gdx.files.internal("insp.mp3"));
		soundExp = Gdx.audio.newSound(Gdx.files.internal("exp.mp3"));
		soundBah = Gdx.audio.newSound(Gdx.files.internal("bah.mp3"));
		soundCough = Gdx.audio.newSound(Gdx.files.internal("kashel.mp3"));

		/*Создаем класс для отрисовки издображений*/
		spriteBatch = new SpriteBatch();

		/*Инициализируем счетчики времени нулевыми значением*/
		stateTime = 0f;
		blowTime = 0f;
	}

	@Override
	public void render() {
		/*Устанавливаем цвет заливки*/
		Gdx.gl.glClearColor(0.2f,0,0.2f,1);
		/*Заливаем экран этим цветом или используемым для глубины*/
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		//System.out.println(expirationAnimation.getKeyFrameIndex(stateTime));

		/*Воспроизведение всей анимационной сцены*/
		scene();


		/*Увеличиваем счетчики времени на время задержки*/
		stateTime += Gdx.graphics.getDeltaTime();
		blowTime += Gdx.graphics.getDeltaTime();
	}


	/*    !---- Кадр из анимации ----! */

	private TextureRegion curentF (){
		/*Если тапнули по экрану...*/
		int index = 0;

		if(Gdx.input.justTouched()){


			/*Получаем номер карда анимации в зависимости от того какая анимация проигрывается в данный момент,
			* так же запускаем и сотанавливаем соответсвующие вдоху и выдоху звуки*/
			if(flag){
				index = expirationAnimation.getKeyFrameIndex(stateTime);
				soundInsp.stop();
				soundExp.play();
			}
			else {
				index = inspirationAnimation.getKeyFrameIndex(stateTime);
				soundExp.stop();
				soundInsp.play();
			}

			/*Инвертируем значение кадра, так как из нулевого нам нужно получить пятый кадр, из первого четвертый и т.д.*/
			/*Берем модуль полученного числа, так как при инверсии мы могли получить отрицательные числа*/
			index = Math.abs(index - 5);

			/*Вичисляем нужное нам время для поиска момента с которого начнет проигрывание
			* новая анимация, зная задержку просто умножаем на номер найденного нами кадра*/
			stateTime = DELAY * index;
			blowTime = 0f;

			/*Меняем флаг а с ним и анимацию на противоположную*/
			flag = !flag;

		}



		if(checkBah){
			currentFrame = bahFrames[bahAnimation.getKeyFrameIndex(stateTime)];

		}
		else if(checkDie){
			currentFrame = deathFrames[deathAnimation.getKeyFrameIndex(stateTime)];
		}
		else {
			if (flag) {
			/*Если во флаге значение истина, то берем из массива с выдохом кадр с номером,
			* соответсвующему времени прошедшему с момента анимации с учетом прописанной нами
			* задержки при создании анимации (у нас 0,5 секунды), тоесть после 0,5 секунд берем второй
			* после 1 сек беерм третий, через полторы берем четвертый*/
				currentFrame = expirationFrames[expirationAnimation.getKeyFrameIndex(stateTime)];
				if (stateTime > DELAY * 6) {
					checkDie = true;
					stateTime = 0;
					soundCough.play();
				}


			} else {
				currentFrame = inspirationFrames[inspirationAnimation.getKeyFrameIndex(stateTime)];
				if (stateTime > DELAY * 6) {
					checkBah = true;
					stateTime = 0;
					soundBah.play();
				}
			}
		}


		return currentFrame;

	}


	/*Компоновка сцены для анимации*/
	private void scene(){
		TextureRegion currentFrame = curentF();

		/*Начинаем отрисовку*/
		spriteBatch.begin();
		/*Рисуем полученный нами кадр исходя из значний флага и текущего времени и ресуем его в пололожении
		* 50 пикс от низа монитора и 50 пикс от левого края монитора, размером 128х128 пикселей, тоесть в */
		spriteBatch.draw(currentFrame, 50, 50, 128, 128);

		if(flag){
			/*Рисуем, номер кадра ветра, по взятому индексу кадра из анимации основываясь на времени анимации*/
			spriteBatch.draw(blowFrames[blowAnimation.getKeyFrameIndex(blowTime)], 179, 50, 128, 128);
		}



		/*Завершаем отрисовку*/
		spriteBatch.end();
	}

}
