package annotation;

import java.lang.annotation.*;



/**
 * 
 * @author karim
 *Declaration de l'annotation Write 
 *Avec une meta-annotation Retention qui permet d'indiquer la durée de vie de l'annotation
 * RetentionPolicy.RUNTIME indique que l'annotation est déclaré dans le .class a la compilation et est 
 * utilisé par la VM a l'execution
 * La meta-annotation target permet de limiter le type d'elements sur lequel l'annotation est utilisé
 * dans notre cas sur une methode
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Write {

}
