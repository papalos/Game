package com.iava.pigs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by sergey on 08.09.2017.
 */

class Hero {

    /*Путь к спрайт листу, его имя с расширением*/
    private String nameSpriteList;

    /*Смена флага ведет к выбору анимации: вдох или выдох*/
    /*Устанавливаем флаг в истину, что означает что при запуске игры визуализация начнется с выдоха*/
    private boolean flag = true;

    /*Определяет взорвался ли персонаж*/
    private boolean checkBah = false;

    /*Задержка меджу кадарми анимации*/
    final float DELAY = 0.50f;

    /*Константы на сколько разбивать спрайтлист*/
    private static final int FRAME_COLS = 6;
    private static final int FRAME_ROWS = 5;

    /*Анимация создание наборов кадров с величиной задержки после отрисовки каждого*/
    private Animation walkAnimation;



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
    TextureRegion expirationFrames [];
    TextureRegion[] inspirationFrames;
    TextureRegion[] blowFrames;
    TextureRegion[] bahFrames;

    /*Анимация из вышеописанных массивов, только теперь с прописаной задержкой между этими кадрами*/
    Animation expirationAnimation;
    Animation inspirationAnimation;
    Animation blowAnimation;
    Animation bahAnimation;

    Sound soundInsp, soundExp, soundBah;

    /** Конструктор
     *  @param spriteList Путь к спрайт листу, его имя с расширение
     *  @param spWidth Ширина одного спрайта
     *  @param spHight Высота одного спрайта
     */
    Hero(String spriteList, int spWidth, int spHight ){
        nameSpriteList = spriteList; //"spritlist1.png"

        /* Создаем переменную для текстуры и Загружаем спрайтлист*/
        Texture walkSheet = new Texture(Gdx.files.internal(nameSpriteList));

        /*Делим его на части (области, кадры) шириной 32х32 пикселя,
        так как область нашего персонажа равна 32 на 32*/
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, spWidth, spHight);

		/*Создаем соответствующие массивы для вдоха и для выдоха, по 6 элементов в каждом, так как в каждой из этих анимаций по 6 кадов*/
        expirationFrames = new TextureRegion[FRAME_COLS];
        inspirationFrames = new TextureRegion[FRAME_COLS];
        blowFrames = new  TextureRegion[FRAME_COLS];
        bahFrames =new TextureRegion[4];

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
        }


		/*Делаем из массивов анимацию, устанавливая интревал между кадрами хранимыми в них*/
        expirationAnimation = new Animation(DELAY, expirationFrames);
        inspirationAnimation = new Animation(DELAY, inspirationFrames);
        blowAnimation = new Animation(DELAY, blowFrames);
        bahAnimation = new Animation(0.07f, bahFrames);

        soundInsp = Gdx.audio.newSound(Gdx.files.internal("insp.mp3"));
        soundExp = Gdx.audio.newSound(Gdx.files.internal("exp.mp3"));
        soundBah = Gdx.audio.newSound(Gdx.files.internal("bah.mp3"));

		/*Создаем класс для отрисовки издображений*/
        spriteBatch = new SpriteBatch();

		/*Инициализируем счетчики времени нулевыми значением*/
        stateTime = 0f;
        blowTime = 0f;

    }
/*
    public TextureRegion[] createTexture(){

        return .....;
    }
    */
}
