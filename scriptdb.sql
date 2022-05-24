/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `eventos` */

CREATE TABLE `eventos` (
  `id_evento` bigint(20) NOT NULL AUTO_INCREMENT,
  `desc_evento` varchar(200) NOT NULL,
  `id_red` bigint(20) NOT NULL,
  `fecha_crea` datetime NOT NULL,
  `ind_proy` int(1) NOT NULL DEFAULT '0',
  `dist_proy` double DEFAULT NULL,
  `id_atributo_fecha` bigint(20) DEFAULT NULL,
  `id_atributo_hora` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_evento`),
  KEY `fk_eventos_redes` (`id_red`),
  CONSTRAINT `fk_eventos_redes` FOREIGN KEY (`id_red`) REFERENCES `redes` (`id_red`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `eventos_atributos` */

CREATE TABLE `eventos_atributos` (
  `id_evento` bigint(20) NOT NULL,
  `id_atributo` bigint(20) NOT NULL,
  `nombre_atributo` varchar(50) NOT NULL,
  `tipo_atributo` varchar(10) NOT NULL,
  PRIMARY KEY (`id_evento`,`id_atributo`),
  CONSTRAINT `fk_eventos_atributos_eventos` FOREIGN KEY (`id_evento`) REFERENCES `eventos` (`id_evento`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `eventos_puntos` */

CREATE TABLE `eventos_puntos` (
  `id_evento` bigint(20) NOT NULL,
  `id_punto` bigint(20) NOT NULL,
  `fecha_punto` datetime NOT NULL,
  `latitud` double NOT NULL,
  `longitud` double NOT NULL,
  `latitud_proy` double DEFAULT NULL,
  `longitud_proy` double DEFAULT NULL,
  `id_red` bigint(20) DEFAULT NULL,
  `id_linea` bigint(20) DEFAULT NULL,
  `num_punto` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_evento`,`id_punto`),
  KEY `fk_eventos_puntos_redes_lineas_det` (`id_red`,`id_linea`,`num_punto`),
  CONSTRAINT `fk_eventos_puntos_eventos` FOREIGN KEY (`id_evento`) REFERENCES `eventos` (`id_evento`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_eventos_puntos_redes_lineas_det` FOREIGN KEY (`id_red`, `id_linea`, `num_punto`) REFERENCES `redes_lineas_det` (`id_red`, `id_linea`, `num_punto`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `eventos_puntos_atributos` */

CREATE TABLE `eventos_puntos_atributos` (
  `id_evento` bigint(20) NOT NULL,
  `id_punto` bigint(20) NOT NULL,
  `id_atributo` bigint(20) NOT NULL,
  `valor_num` double DEFAULT NULL,
  `valor_tex` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_evento`,`id_punto`,`id_atributo`),
  KEY `fk_eventos_puntos_atributos_eventos_atributos` (`id_evento`,`id_atributo`),
  CONSTRAINT `fk_eventos_puntos_atributos_eventos_atributos` FOREIGN KEY (`id_evento`, `id_atributo`) REFERENCES `eventos_atributos` (`id_evento`, `id_atributo`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_eventos_puntos_atributos_eventos_puntos` FOREIGN KEY (`id_evento`, `id_punto`) REFERENCES `eventos_puntos` (`id_evento`, `id_punto`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `formatos_texto` */

CREATE TABLE `formatos_texto` (
  `id_formato` bigint(20) NOT NULL AUTO_INCREMENT,
  `texto_formato` varchar(50) NOT NULL,
  `formato` varchar(50) NOT NULL,
  `tipo_formato` varchar(10) NOT NULL,
  `orden` int(10) NOT NULL,
  `ind_activo` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_formato`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `formatos_texto` */

insert  into `formatos_texto`(`id_formato`,`texto_formato`,`formato`,`tipo_formato`,`orden`,`ind_activo`) values (1,'mm/dd/yyyy','%m/%d/%Y','date',1,1),(2,'mm-dd-yyyy','%m-%d-%Y','date',2,1),(3,'mm/dd/yy','%m/%d/%y','date',3,1),(4,'mm-dd-yy','%m-%d-%y','date',4,1),(5,'yyyy/mm/dd','%Y/%m/%d','date',5,1),(6,'yyyy-mm-dd','%Y-%m-%d','date',6,1),(7,'yy/mm/dd','%y/%m/%d','date',7,1),(8,'yy-mm-dd','%y-%m-%d','date',8,1),(9,'dd/mm/yyyy','%d/%m/%Y','date',9,1),(10,'dd-mm-yyyy','%d-%m-%Y','date',10,1),(11,'dd/mm/yy','%d/%m/%y','date',11,1),(12,'dd-mm-yy','%d-%m-%y','date',12,1),(13,'h12:mm:ss am/pm','%l:%i:%s %p','time',1,1),(14,'h12:mm am/pm','%l:%i %p','time',2,1),(15,'h24:mm:ss','%k:%i:%s','time',3,1),(16,'h24:mm','%k:%i','time',4,1);

/*Table structure for table `funciones_nucleo` */

CREATE TABLE `funciones_nucleo` (
  `id_funcion` int(10) NOT NULL,
  `nombre_funcion` varchar(100) NOT NULL,
  `ind_activo` int(1) NOT NULL,
  PRIMARY KEY (`id_funcion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `funciones_nucleo` */

insert  into `funciones_nucleo`(`id_funcion`,`nombre_funcion`,`ind_activo`) values (1,'Gaussian',1),(2,'Epanechnikov',1),(3,'Minimum Variance',1),(4,'Uniform',1),(5,'Triangular',1);

/*Table structure for table `knet_resultados` */

CREATE TABLE `knet_resultados` (
  `id_knet` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_evento` bigint(20) NOT NULL,
  `distancia_ini` double NOT NULL,
  `distancia_fin` double NOT NULL,
  `incremento_dist` double NOT NULL,
  `cant_puntos` int(10) NOT NULL,
  `cant_aleatorios` int(10) NOT NULL,
  `fecha_resultado` datetime NOT NULL,
  `filtros_resultado` text,
  PRIMARY KEY (`id_knet`),
  KEY `fk_knet_resultados_eventos` (`id_evento`),
  CONSTRAINT `fk_knet_resultados_eventos` FOREIGN KEY (`id_evento`) REFERENCES `eventos` (`id_evento`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `knet_valores` */

CREATE TABLE `knet_valores` (
  `id_knet` bigint(20) NOT NULL,
  `distancia_knet` double NOT NULL,
  `valor` double NOT NULL,
  `limite_min` double NOT NULL,
  `limite_max` double NOT NULL,
  PRIMARY KEY (`id_knet`,`distancia_knet`),
  CONSTRAINT `fk_knet_valores_knet_resultados` FOREIGN KEY (`id_knet`) REFERENCES `knet_resultados` (`id_knet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `netkde_lixels` */

CREATE TABLE `netkde_lixels` (
  `id_netkde` bigint(20) NOT NULL,
  `id_lixel` bigint(20) NOT NULL,
  `id_red` bigint(20) NOT NULL,
  `id_linea` bigint(20) NOT NULL,
  `num_punto` bigint(20) NOT NULL,
  `lat_lxcenter` double NOT NULL,
  `lon_lxcenter` double NOT NULL,
  `largo_lixel` double NOT NULL,
  `cantidad_puntos` int(10) NOT NULL,
  `densidad_lixel` double NOT NULL,
  PRIMARY KEY (`id_netkde`,`id_lixel`),
  KEY `fk_netkde_lixels_netkde_resultados` (`id_netkde`),
  KEY `fk_netkde_lixels_redes_lineas_det` (`id_red`,`id_linea`,`num_punto`),
  CONSTRAINT `fk_netkde_lixels_netkde_resultados` FOREIGN KEY (`id_netkde`) REFERENCES `netkde_resultados` (`id_netkde`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_netkde_lixels_redes_lineas_det` FOREIGN KEY (`id_red`, `id_linea`, `num_punto`) REFERENCES `redes_lineas_det` (`id_red`, `id_linea`, `num_punto`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `netkde_lixels_det` */

CREATE TABLE `netkde_lixels_det` (
  `id_netkde` bigint(20) NOT NULL,
  `id_lixel` bigint(20) NOT NULL,
  `num_punto` bigint(20) NOT NULL,
  `latitud` double NOT NULL,
  `longitud` double NOT NULL,
  `largo_segmento` double NOT NULL,
  PRIMARY KEY (`id_netkde`,`id_lixel`,`num_punto`),
  CONSTRAINT `fk_netkde_lixels_det_netkde_lixels` FOREIGN KEY (`id_netkde`, `id_lixel`) REFERENCES `netkde_lixels` (`id_netkde`, `id_lixel`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `netkde_lixels_puntos` */

CREATE TABLE `netkde_lixels_puntos` (
  `id_netkde` bigint(20) NOT NULL,
  `id_lixel` bigint(20) NOT NULL,
  `id_evento` bigint(20) NOT NULL,
  `id_punto` bigint(20) NOT NULL,
  PRIMARY KEY (`id_netkde`,`id_lixel`,`id_evento`,`id_punto`),
  KEY `fk_netkde_lixels_puntos_eventos_puntos` (`id_evento`,`id_punto`),
  CONSTRAINT `fk_netkde_lixels_puntos_eventos_puntos` FOREIGN KEY (`id_evento`, `id_punto`) REFERENCES `eventos_puntos` (`id_evento`, `id_punto`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_netkde_lixels_puntos_netkde_lixels` FOREIGN KEY (`id_netkde`, `id_lixel`) REFERENCES `netkde_lixels` (`id_netkde`, `id_lixel`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `netkde_resultados` */

CREATE TABLE `netkde_resultados` (
  `id_netkde` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_evento` bigint(20) NOT NULL,
  `ancho_banda` double NOT NULL,
  `largo_lixel` double NOT NULL,
  `id_funcion` int(10) NOT NULL,
  `cant_puntos` int(10) NOT NULL,
  `fecha_resultado` datetime NOT NULL,
  `filtros_resultado` text,
  PRIMARY KEY (`id_netkde`),
  KEY `fk_netkde_resultados_eventos` (`id_evento`),
  KEY `fk_netkde_resultados_funciones_nucleo` (`id_funcion`),
  CONSTRAINT `fk_netkde_resultados_eventos` FOREIGN KEY (`id_evento`) REFERENCES `eventos` (`id_evento`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_netkde_resultados_funciones_nucleo` FOREIGN KEY (`id_funcion`) REFERENCES `funciones_nucleo` (`id_funcion`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `redes` */

CREATE TABLE `redes` (
  `id_red` bigint(20) NOT NULL AUTO_INCREMENT,
  `desc_red` varchar(200) NOT NULL,
  `id_sistema` bigint(20) NOT NULL,
  `fecha_crea` datetime NOT NULL,
  `ind_cierre_nodos` int(1) NOT NULL DEFAULT '0',
  `dist_cierre_nodos` double DEFAULT NULL,
  PRIMARY KEY (`id_red`),
  KEY `fk_redes_sistemas_coordenadas` (`id_sistema`),
  CONSTRAINT `fk_redes_sistemas_coordenadas` FOREIGN KEY (`id_sistema`) REFERENCES `sistemas_coordenadas` (`id_sistema`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `redes_atributos` */

CREATE TABLE `redes_atributos` (
  `id_red` bigint(20) NOT NULL,
  `id_atributo` bigint(20) NOT NULL,
  `nombre_atributo` varchar(50) NOT NULL,
  `tipo_atributo` varchar(10) NOT NULL,
  PRIMARY KEY (`id_red`,`id_atributo`),
  CONSTRAINT `fk_redes_atributos_redes` FOREIGN KEY (`id_red`) REFERENCES `redes` (`id_red`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `redes_lineas` */

CREATE TABLE `redes_lineas` (
  `id_red` bigint(20) NOT NULL,
  `id_linea` bigint(20) NOT NULL,
  `largo_linea` double NOT NULL DEFAULT '0',
  `largo_acumulado` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_red`,`id_linea`),
  CONSTRAINT `fk_redes_lineas_redes` FOREIGN KEY (`id_red`) REFERENCES `redes` (`id_red`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `redes_lineas_atributos` */

CREATE TABLE `redes_lineas_atributos` (
  `id_red` bigint(20) NOT NULL,
  `id_linea` bigint(20) NOT NULL,
  `id_atributo` bigint(20) NOT NULL,
  `valor_num` double DEFAULT NULL,
  `valor_tex` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_red`,`id_linea`,`id_atributo`),
  KEY `fk_redes_lineas_atributos_redes_atributos` (`id_red`,`id_atributo`),
  CONSTRAINT `fk_redes_lineas_atributos_redes_atributos` FOREIGN KEY (`id_red`, `id_atributo`) REFERENCES `redes_atributos` (`id_red`, `id_atributo`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_redes_lineas_atributos_redes_lineas` FOREIGN KEY (`id_red`, `id_linea`) REFERENCES `redes_lineas` (`id_red`, `id_linea`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `redes_lineas_det` */

CREATE TABLE `redes_lineas_det` (
  `id_red` bigint(20) NOT NULL,
  `id_linea` bigint(20) NOT NULL,
  `num_punto` bigint(20) NOT NULL,
  `latitud` double NOT NULL,
  `longitud` double NOT NULL,
  `largo_segmento` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_red`,`id_linea`,`num_punto`),
  KEY `ix_redes_lineas_det_01` (`latitud`),
  KEY `ix_redes_lineas_det_02` (`longitud`),
  CONSTRAINT `fk_redes_lineas_det_redes_lineas` FOREIGN KEY (`id_red`, `id_linea`) REFERENCES `redes_lineas` (`id_red`, `id_linea`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `sistemas_coordenadas` */

CREATE TABLE `sistemas_coordenadas` (
  `id_sistema` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre_sistema` varchar(100) NOT NULL,
  `id_unidad` bigint(20) NOT NULL,
  PRIMARY KEY (`id_sistema`),
  KEY `fk_sistemas_coordenadas_unidades_medida` (`id_unidad`),
  CONSTRAINT `fk_sistemas_coordenadas_unidades_medida` FOREIGN KEY (`id_unidad`) REFERENCES `unidades_medida` (`id_unidad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sistemas_coordenadas` */

insert  into `sistemas_coordenadas`(`id_sistema`,`nombre_sistema`,`id_unidad`) values (1,'Geographic Coordinates',1),(2,'Planar Coordinates (meters)',2),(3,'Planar Coordinates (100 kilometers)',4);

/*Table structure for table `tmp_eventos` */

CREATE TABLE `tmp_eventos` (
  `id_evento` bigint(20) NOT NULL AUTO_INCREMENT,
  `desc_evento` varchar(200) NOT NULL,
  `id_red` bigint(20) NOT NULL,
  PRIMARY KEY (`id_evento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_eventos_atributos` */

CREATE TABLE `tmp_eventos_atributos` (
  `id_evento` bigint(20) NOT NULL,
  `id_atributo` bigint(20) NOT NULL,
  `nombre_atributo` varchar(50) NOT NULL,
  `tipo_atributo` varchar(10) NOT NULL,
  PRIMARY KEY (`id_evento`,`id_atributo`),
  CONSTRAINT `fk_tmp_eventos_atributos_eventos` FOREIGN KEY (`id_evento`) REFERENCES `tmp_eventos` (`id_evento`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_eventos_puntos` */

CREATE TABLE `tmp_eventos_puntos` (
  `id_evento` bigint(20) NOT NULL,
  `id_punto` bigint(20) NOT NULL,
  `fecha_punto` datetime NOT NULL,
  `latitud` double NOT NULL,
  `longitud` double NOT NULL,
  `id_red` bigint(20) DEFAULT NULL,
  `id_linea` bigint(20) DEFAULT NULL,
  `num_punto` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_evento`,`id_punto`),
  CONSTRAINT `fk_tmp_eventos_puntos_eventos` FOREIGN KEY (`id_evento`) REFERENCES `tmp_eventos` (`id_evento`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_eventos_puntos_atributos` */

CREATE TABLE `tmp_eventos_puntos_atributos` (
  `id_evento` bigint(20) NOT NULL,
  `id_punto` bigint(20) NOT NULL,
  `id_atributo` bigint(20) NOT NULL,
  `valor_num` double DEFAULT NULL,
  `valor_tex` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_evento`,`id_punto`,`id_atributo`),
  KEY `fk_tmp_eventos_puntos_atributos_eventos_atributos` (`id_evento`,`id_atributo`),
  CONSTRAINT `fk_tmp_eventos_puntos_atributos_eventos_atributos` FOREIGN KEY (`id_evento`, `id_atributo`) REFERENCES `tmp_eventos_atributos` (`id_evento`, `id_atributo`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_tmp_eventos_puntos_atributos_eventos_puntos` FOREIGN KEY (`id_evento`, `id_punto`) REFERENCES `tmp_eventos_puntos` (`id_evento`, `id_punto`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_knet_resultados` */

CREATE TABLE `tmp_knet_resultados` (
  `id_knet` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_evento` bigint(20) NOT NULL,
  `distancia_ini` double NOT NULL,
  `distancia_fin` double NOT NULL,
  `incremento_dist` double NOT NULL,
  `cant_puntos` int(10) NOT NULL,
  `cant_aleatorios` int(10) NOT NULL,
  `filtros_resultado` text,
  PRIMARY KEY (`id_knet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_knet_valores` */

CREATE TABLE `tmp_knet_valores` (
  `id_knet` bigint(20) NOT NULL,
  `distancia_knet` double NOT NULL,
  `valor` double NOT NULL,
  `limite_min` double NOT NULL,
  `limite_max` double NOT NULL,
  PRIMARY KEY (`id_knet`,`distancia_knet`),
  CONSTRAINT `fk_tmp_knet_valores_knet_resultados` FOREIGN KEY (`id_knet`) REFERENCES `tmp_knet_resultados` (`id_knet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_netkde_lixels` */

CREATE TABLE `tmp_netkde_lixels` (
  `id_netkde` bigint(20) NOT NULL,
  `id_lixel` bigint(20) NOT NULL,
  `id_red` bigint(20) NOT NULL,
  `id_linea` bigint(20) NOT NULL,
  `num_punto` bigint(20) NOT NULL,
  `lat_lxcenter` double NOT NULL,
  `lon_lxcenter` double NOT NULL,
  `largo_lixel` double NOT NULL,
  `cantidad_puntos` int(10) NOT NULL,
  `densidad_lixel` double NOT NULL,
  PRIMARY KEY (`id_netkde`,`id_lixel`),
  CONSTRAINT `fk_tmp_netkde_lixels_tmp_netkde_resultados` FOREIGN KEY (`id_netkde`) REFERENCES `tmp_netkde_resultados` (`id_netkde`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_netkde_lixels_det` */

CREATE TABLE `tmp_netkde_lixels_det` (
  `id_netkde` bigint(20) NOT NULL,
  `id_lixel` bigint(20) NOT NULL,
  `num_punto` bigint(20) NOT NULL,
  `latitud` double NOT NULL,
  `longitud` double NOT NULL,
  `largo_segmento` double NOT NULL,
  PRIMARY KEY (`id_netkde`,`id_lixel`,`num_punto`),
  CONSTRAINT `fk_tmp_netkde_lixels_det_tmp_netkde_lixels` FOREIGN KEY (`id_netkde`, `id_lixel`) REFERENCES `tmp_netkde_lixels` (`id_netkde`, `id_lixel`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_netkde_lixels_puntos` */

CREATE TABLE `tmp_netkde_lixels_puntos` (
  `id_netkde` bigint(20) NOT NULL,
  `id_lixel` bigint(20) NOT NULL,
  `id_evento` bigint(20) NOT NULL,
  `id_punto` bigint(20) NOT NULL,
  PRIMARY KEY (`id_netkde`,`id_lixel`,`id_evento`,`id_punto`),
  CONSTRAINT `fk_tmp_netkde_lixels_puntos_tmp_netkde_lixels` FOREIGN KEY (`id_netkde`, `id_lixel`) REFERENCES `tmp_netkde_lixels` (`id_netkde`, `id_lixel`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_netkde_resultados` */

CREATE TABLE `tmp_netkde_resultados` (
  `id_netkde` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_evento` bigint(20) NOT NULL,
  `ancho_banda` double NOT NULL,
  `largo_lixel` double NOT NULL,
  `id_funcion` int(10) NOT NULL,
  `cant_puntos` int(10) NOT NULL,
  `filtros_resultado` text,
  PRIMARY KEY (`id_netkde`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_redes` */

CREATE TABLE `tmp_redes` (
  `id_red` bigint(20) NOT NULL AUTO_INCREMENT,
  `desc_red` varchar(200) NOT NULL,
  `id_sistema` bigint(20) NOT NULL,
  PRIMARY KEY (`id_red`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_redes_atributos` */

CREATE TABLE `tmp_redes_atributos` (
  `id_red` bigint(20) NOT NULL,
  `id_atributo` bigint(20) NOT NULL,
  `nombre_atributo` varchar(50) NOT NULL,
  `tipo_atributo` varchar(10) NOT NULL,
  PRIMARY KEY (`id_red`,`id_atributo`),
  CONSTRAINT `fk_tmp_redes_atributos_tmp_redes` FOREIGN KEY (`id_red`) REFERENCES `tmp_redes` (`id_red`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_redes_lineas` */

CREATE TABLE `tmp_redes_lineas` (
  `id_red` bigint(20) NOT NULL,
  `id_linea` bigint(20) NOT NULL,
  PRIMARY KEY (`id_red`,`id_linea`),
  CONSTRAINT `fk_tmp_redes_lineas_tmp_redes` FOREIGN KEY (`id_red`) REFERENCES `tmp_redes` (`id_red`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_redes_lineas_atributos` */

CREATE TABLE `tmp_redes_lineas_atributos` (
  `id_red` bigint(20) NOT NULL,
  `id_linea` bigint(20) NOT NULL,
  `id_atributo` bigint(20) NOT NULL,
  `valor_num` double DEFAULT NULL,
  `valor_tex` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_red`,`id_linea`,`id_atributo`),
  KEY `fk_tmp_redes_lineas_atributos_tmp_redes_atributos` (`id_red`,`id_atributo`),
  CONSTRAINT `fk_tmp_redes_lineas_atributos_tmp_redes_atributos` FOREIGN KEY (`id_red`, `id_atributo`) REFERENCES `tmp_redes_atributos` (`id_red`, `id_atributo`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_tmp_redes_lineas_atributos_tmp_redes_lineas` FOREIGN KEY (`id_red`, `id_linea`) REFERENCES `tmp_redes_lineas` (`id_red`, `id_linea`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `tmp_redes_lineas_det` */

CREATE TABLE `tmp_redes_lineas_det` (
  `id_red` bigint(20) NOT NULL,
  `id_linea` bigint(20) NOT NULL,
  `num_punto` bigint(20) NOT NULL,
  `latitud` double NOT NULL,
  `longitud` double NOT NULL,
  PRIMARY KEY (`id_red`,`id_linea`,`num_punto`),
  CONSTRAINT `fk_tmp_redes_lineas_det_tmp_redes_lineas` FOREIGN KEY (`id_red`, `id_linea`) REFERENCES `tmp_redes_lineas` (`id_red`, `id_linea`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `unidades_medida` */

CREATE TABLE `unidades_medida` (
  `id_unidad` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre_unidad` varchar(50) NOT NULL,
  `factor_metros` double NOT NULL,
  `ind_grados` int(1) NOT NULL,
  PRIMARY KEY (`id_unidad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `unidades_medida` */

insert  into `unidades_medida`(`id_unidad`,`nombre_unidad`,`factor_metros`,`ind_grados`) values (1,'Degrees',0,1),(2,'Meters',1,0),(3,'Kilometers',1000,0),(4,'100 Kilometers',100000,0);

/* Function  structure for function  `fu_arctanh` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_arctanh`(in_z DOUBLE) RETURNS double
    DETERMINISTIC
BEGIN
    DECLARE l_resultado DOUBLE;
    
    SET l_resultado=(LN(1+in_z)-LN(1-in_z))/2;
    
    RETURN l_resultado;
END */$$
DELIMITER ;

/* Function  structure for function  `fu_calcular_distancia_punto_linea` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_calcular_distancia_punto_linea`(in_latitud_punto DOUBLE,
                                                    in_longitud_punto DOUBLE,
                                                    in_latitud_ini DOUBLE,
                                                    in_longitud_ini DOUBLE,
                                                    in_latitud_fin DOUBLE,
                                                    in_longitud_fin DOUBLE,
                                                    in_factor_metros DOUBLE,
                                                    in_ind_grados INT(1)) RETURNS double
    DETERMINISTIC
BEGIN
    /*
    IMPORTANTE:
    Esta función halla la distancia más corta (perpendicular) entre un punto una la línea recta entendida
    como una línea infinita, por lo tanto es posible que la distancia hallada corresponda a un punto
    proyectado fuera del segmento de línea recta dado por los dos puntos que lo definen.
    */
    DECLARE l_latitud_punto DOUBLE;
    DECLARE l_longitud_punto DOUBLE;
    DECLARE l_latitud_ini DOUBLE;
    DECLARE l_longitud_ini DOUBLE;
    DECLARE l_latitud_fin DOUBLE;
    DECLARE l_longitud_fin DOUBLE;
    DECLARE l_pendiente DOUBLE;
    DECLARE l_cruce_y DOUBLE;
    DECLARE l_distancia DOUBLE;
    
    /*Se verifica si se está trabajando con coordenadas geográficas*/
    IF in_ind_grados=1 THEN
        /*Se deben convertir las coordenadas geográficas a coordenadas cartesianas, se toma como punto de origen el punto recibido*/
        SET l_latitud_punto=0, l_longitud_punto=0;
        SET l_latitud_ini=fu_transformar_latitud_plana(in_latitud_ini, in_latitud_punto, in_longitud_punto, in_factor_metros);
        SET l_longitud_ini=fu_transformar_longitud_plana(in_longitud_ini, in_latitud_punto, in_longitud_punto, in_factor_metros);
        SET l_latitud_fin=fu_transformar_latitud_plana(in_latitud_fin, in_latitud_punto, in_longitud_punto, in_factor_metros);
        SET l_longitud_fin=fu_transformar_longitud_plana(in_longitud_fin, in_latitud_punto, in_longitud_punto, in_factor_metros);
    ELSE
        SET l_latitud_punto=in_latitud_punto,
        l_longitud_punto=in_longitud_punto,
        l_latitud_ini=in_latitud_ini,
        l_longitud_ini=in_longitud_ini,
        l_latitud_fin=in_latitud_fin,
        l_longitud_fin=in_longitud_fin;
    END IF;
    
    /*Se calcula la distancia*/
    SET l_distancia=NULL;
    IF l_longitud_ini=l_longitud_fin THEN
        /*Se trata de una línea vertical*/
        SET l_distancia=ABS(l_longitud_punto-l_longitud_ini);
    ELSEIF l_latitud_ini=l_latitud_fin THEN
        /*Se trata de una línea horizontal*/
        SET l_distancia=ABS(l_latitud_punto-l_latitud_ini);
    ELSE
        SET l_pendiente=(l_latitud_fin-l_latitud_ini)/(l_longitud_fin-l_longitud_ini);
        SET l_cruce_y=l_latitud_ini-l_pendiente*l_longitud_ini;
        
        SET l_distancia=ABS(l_pendiente*l_longitud_punto-l_latitud_punto+l_cruce_y)/SQRT(POW(l_pendiente, 2)+1);
    END IF;
    
    RETURN l_distancia;
END */$$
DELIMITER ;

/* Function  structure for function  `fu_calcular_largo_linea_puntos` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_calcular_largo_linea_puntos`(in_latitud_ini DOUBLE,
                                                 in_longitud_ini DOUBLE,
                                                 in_latitud_fin DOUBLE,
                                                 in_longitud_fin DOUBLE,
                                                 in_factor_metros DOUBLE,
                                                 in_ind_grados INT(1)) RETURNS double
    DETERMINISTIC
BEGIN
    /*
    IMPORTANTE:
    La distancia calculada por esta función siempre está dada en metros.
    */
    DECLARE l_distancia DOUBLE;
    DECLARE l_geoide DOUBLE DEFAULT 6372795.477598;
    
    IF in_ind_grados=1 THEN
        SET l_distancia = l_geoide * IFNULL(ACOS(SIN(RADIANS(in_latitud_ini)) * SIN(RADIANS(in_latitud_fin)) + cos(RADIANS(in_latitud_ini)) * cos(RADIANS(in_latitud_fin)) * cos(RADIANS(in_longitud_ini) - RADIANS(in_longitud_fin))), 0); 
    ELSE
        SET l_distancia = SQRT(POW(in_latitud_ini - in_latitud_fin, 2) + POW(in_longitud_ini - in_longitud_fin, 2)) * in_factor_metros;
    END IF;
    
    RETURN l_distancia;
END */$$
DELIMITER ;

/* Function  structure for function  `fu_cosh` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_cosh`(in_z DOUBLE) RETURNS double
    DETERMINISTIC
BEGIN
    DECLARE l_resultado DOUBLE;
    
    SET l_resultado=(EXP(in_z)+EXP(-in_z))/2;
    
    RETURN l_resultado;
END */$$
DELIMITER ;

/* Function  structure for function  `fu_estimar_distancia_punto_linea` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_estimar_distancia_punto_linea`(in_latitud_punto DOUBLE,
                                                   in_longitud_punto DOUBLE,
                                                   in_latitud_ini DOUBLE,
                                                   in_longitud_ini DOUBLE,
                                                   in_latitud_fin DOUBLE,
                                                   in_longitud_fin DOUBLE,
                                                   in_factor_latitud DOUBLE,
                                                   in_factor_longitud INT(1)) RETURNS double
    DETERMINISTIC
BEGIN
    DECLARE l_m DOUBLE;
    DECLARE l_b DOUBLE;
    DECLARE l_x_proy DOUBLE;
    DECLARE l_y_proy DOUBLE;
    DECLARE l_distancia_x DOUBLE;
    DECLARE l_distancia_y DOUBLE;
    DECLARE l_distancia DOUBLE;
    
    SET l_distancia=NULL;
    IF in_longitud_ini=in_longitud_fin THEN
        /*Línea vertical*/
        SET l_distancia=ABS(in_longitud_punto-in_longitud_ini)*in_factor_longitud;
    ELSEIF in_latitud_ini=in_latitud_fin THEN
        /*Línea horizontal*/
        SET l_distancia=ABS(in_latitud_punto-in_latitud_ini)*in_factor_latitud;
    ELSE
        /*Pendiente y corte*/
        SET l_m=(in_latitud_fin-in_latitud_ini)/(in_longitud_fin-in_longitud_ini);
        SET l_b=in_latitud_ini-l_m*in_longitud_ini;
        
        /*distancia vertical*/
        SET l_y_proy=l_m*in_longitud_punto+l_b;
        SET l_distancia_y=ABS(l_y_proy-in_latitud_punto)*in_factor_latitud;
        
        /*distancia horizontal*/
        SET l_x_proy=(in_latitud_punto-l_b)/l_m;
        SET l_distancia_x=ABS(l_x_proy-in_longitud_punto)*in_factor_longitud;
        
        SET l_distancia=CASE WHEN l_distancia_x<l_distancia_y THEN l_distancia_x ELSE l_distancia_y END;
    END IF;
    
    RETURN l_distancia;
END */$$
DELIMITER ;

/* Function  structure for function  `fu_geograficas_a_utm` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_geograficas_a_utm`(in_tipo VARCHAR(1),
                                       in_latitud DOUBLE,
                                       in_longitud DOUBLE) RETURNS double
    DETERMINISTIC
BEGIN
    /*
    IMPORTANTE:
    Esta función retorna una de las coordenadas que describen una ubicación en el sistema de
    coordenadas universal transversal de Mercator (UTM), las coordenadas X y Y proyectadas
    están dadas en metros.
    Los tipos de coordenadas de entrada válidos son 'X' y 'Y' para las coordenadas, y 'Z' y 'H'
    para la zona y hemisferio respectivamente.
    */
    DECLARE l_x DOUBLE;
    DECLARE l_y DOUBLE;
    DECLARE l_zona INT(4);
    DECLARE l_hemisferio INT(4);
    DECLARE l_long0, l_latitud, l_longitud DOUBLE;
    DECLARE l_k0, l_x0, l_y0 DOUBLE;
    DECLARE l_f, l_n, l_a, l_aa, l_a1, l_a2, l_a3, l_b1, l_b2, l_b3, l_d1, l_d2, l_d3 DOUBLE;
    DECLARE l_t, l_ep, l_np, l_r, l_tt DOUBLE;
    DECLARE l_resultado DOUBLE;
    
    SET l_resultado=NULL;
    IF UPPER(in_tipo) IN ('X', 'Y') THEN
        /*Se calculan los valores base*/
        SET l_latitud=RADIANS(in_latitud);
        SET l_longitud=RADIANS(in_longitud);
        SET l_k0=0.9996;
        SET l_x0=500;
        SET l_y0=CASE WHEN l_latitud>=0 THEN 0 ELSE 10000 END;
        SET l_f=1/298.257223563;
        SET l_n=l_f/(2-l_f);
        SET l_a=6378.137;
        SET l_aa=(l_a/(1+l_n))*(1+POW(l_n, 2)/4+POW(l_n, 4)/64+POW(l_n, 6)/256+POW(l_n, 8)*25/16384+POW(l_n, 10)*49/65536);
        SET l_a1=l_n/2-POW(l_n, 2)*2/3+POW(l_n, 3)*5/16;
        SET l_a2=POW(l_n, 2)*13/48-POW(l_n, 3)*3/5;
        SET l_a3=POW(l_n, 3)*61/240;
        SET l_b1=l_n/2-POW(l_n, 2)*2/3+POW(l_n, 3)*37/96;
        SET l_b2=POW(l_n, 2)/48+POW(l_n, 3)/15;
        SET l_b3=POW(l_n, 3)*17/480;
        SET l_d1=l_n*2-POW(l_n, 2)*2/3-POW(l_n, 3)*2;
        SET l_d2=POW(l_n, 2)*7/3-POW(l_n, 3)*8/5;
        SET l_d3=POW(l_n, 3)*56/15;
        
        /*Se calculan los valores intermedios*/
        SET l_long0=RADIANS(FLOOR(in_longitud/6)*6+3);
        SET l_t=fu_sinh(fu_arctanh(SIN(l_latitud))-(2*SQRT(l_n)/(1+l_n))*fu_arctanh((2*SQRT(l_n)/(1+l_n))*SIN(l_latitud)));
        SET l_ep=ATAN(l_t/COS(l_longitud-l_long0));
        SET l_np=fu_arctanh(SIN(l_longitud-l_long0)/SQRT(1+POW(l_t, 2)));
        SET l_r=1 + 2*l_a1*COS(2*l_ep)*fu_cosh(2*l_np) + 4*l_a2*COS(4*l_ep)*fu_cosh(4*l_np) + 6*l_a3*COS(6*l_ep)*fu_cosh(6*l_np);
        SET l_tt=2*l_a1*SIN(2*l_ep)*fu_sinh(2*l_np) + 4*l_a2*SIN(4*l_ep)*fu_sinh(4*l_np) + 6*l_a3*SIN(6*l_ep)*fu_sinh(6*l_np);
        
        /*Se calculan los valores de las coordenadas*/
        SET l_x=l_x0+l_k0*l_aa*(l_np + l_a1*COS(2*l_ep)*fu_sinh(2*l_np) + l_a2*COS(4*l_ep)*fu_sinh(4*l_np) + l_a3*COS(6*l_ep)*fu_sinh(6*l_np));
        SET l_y=l_y0+l_k0*l_aa*(l_ep + l_a1*SIN(2*l_ep)*fu_cosh(2*l_np) + l_a2*SIN(4*l_ep)*fu_cosh(4*l_np) + l_a3*SIN(6*l_ep)*fu_cosh(6*l_np));
        
        IF UPPER(in_tipo)='X' THEN
            SET l_resultado=l_x*1000;
        ELSE
            SET l_resultado=l_y*1000;
        END IF;
    ELSEIF UPPER(in_tipo)='Z' THEN
        SET l_zona=FLOOR((in_longitud+180)/6)+1;
        
        SET l_resultado=l_zona;
    ELSEIF UPPER(in_tipo)='H' THEN
        SET l_hemisferio=CASE WHEN in_latitud>=0 THEN 1 ELSE -1 END;
        
        SET l_resultado=l_hemisferio;
    END IF;
    
    RETURN l_resultado;
END */$$
DELIMITER ;

/* Function  structure for function  `fu_sinh` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_sinh`(in_z DOUBLE) RETURNS double
    DETERMINISTIC
BEGIN
    DECLARE l_resultado DOUBLE;
    
    SET l_resultado=(EXP(in_z)-EXP(-in_z))/2;
    
    RETURN l_resultado;
END */$$
DELIMITER ;

/* Function  structure for function  `fu_transformar_latitud_plana` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_transformar_latitud_plana`(in_latitud DOUBLE,
                                               in_latitud_punto DOUBLE,
                                               in_longitud_punto DOUBLE,
                                               in_factor_metros DOUBLE) RETURNS double
    DETERMINISTIC
BEGIN
    /*
    IMPORTANTE:
    Esta función halla la proyección de una coordenada de latitud en coordenadas geográficas
    con respecto a un punto base que en la proyección se toma como (0, 0).
    */
    DECLARE l_latitud DOUBLE;
    
    SET l_latitud=fu_calcular_largo_linea_puntos(in_latitud_punto, in_longitud_punto, in_latitud, in_longitud_punto, in_factor_metros, 1);
    IF in_latitud<in_latitud_punto THEN
        SET l_latitud=l_latitud*-1;
    END IF;
    
    RETURN l_latitud;
END */$$
DELIMITER ;

/* Function  structure for function  `fu_transformar_longitud_plana` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_transformar_longitud_plana`(in_longitud DOUBLE,
                                                in_latitud_punto DOUBLE,
                                                in_longitud_punto DOUBLE,
                                                in_factor_metros DOUBLE) RETURNS double
    DETERMINISTIC
BEGIN
    /*
    IMPORTANTE:
    Esta función halla la proyección de una coordenada de longitud en coordenadas geográficas
    con respecto a un punto base que en la proyección se toma como (0, 0).
    */
    DECLARE l_longitud DOUBLE;
    
    SET l_longitud=fu_calcular_largo_linea_puntos(in_latitud_punto, in_longitud_punto, in_latitud_punto, in_longitud, in_factor_metros, 1);
    IF in_longitud<in_longitud_punto THEN
        SET l_longitud=l_longitud*-1;
    END IF;
    
    RETURN l_longitud;
END */$$
DELIMITER ;

/* Function  structure for function  `fu_utm_a_geograficas` */

DELIMITER $$

/*!50003 CREATE FUNCTION `fu_utm_a_geograficas`(in_tipo VARCHAR(3),
                                       in_x DOUBLE,
                                       in_y DOUBLE,
                                       in_zona INT(4),
                                       in_hemisferio INT(4)) RETURNS double
    DETERMINISTIC
BEGIN
    /*
    IMPORTANTE:
    Esta función retorna una de las coordenadas que describen una ubicación en el sistema de
    coordenadas geográficas, las coordenadas de entrada deben estar dadas en metros .
    Los tipos de coordenadas de entrada válidos son 'LAT' y 'LON' para la latitud y la
    longitud respectivamente.
    */
    DECLARE l_x, l_y DOUBLE;
    DECLARE l_latitud, l_longitud, l_long0 DOUBLE;
    DECLARE l_k0, l_x0, l_y0 DOUBLE;
    DECLARE l_f, l_n, l_a, l_aa, l_a1, l_a2, l_a3, l_b1, l_b2, l_b3, l_d1, l_d2, l_d3 DOUBLE;
    DECLARE l_e, l_nn, l_ep, l_np, l_rp, l_tp, l_xx DOUBLE;
    DECLARE l_resultado DOUBLE;
    
    SET l_resultado=NULL;
    
    /*Se calculan los valores base*/
    SET l_x=in_x/1000;
    SET l_y=in_y/1000;
    SET l_k0=0.9996;
    SET l_x0=500;
    SET l_y0=CASE WHEN in_hemisferio>0 THEN 0 ELSE 10000 END;
    SET l_f=1/298.257223563;
    SET l_n=l_f/(2-l_f);
    SET l_a=6378.137;
    SET l_aa=(l_a/(1+l_n))*(1+POW(l_n, 2)/4+POW(l_n, 4)/64+POW(l_n, 6)/256+POW(l_n, 8)*25/16384+POW(l_n, 10)*49/65536);
    SET l_a1=l_n/2-POW(l_n, 2)*2/3+POW(l_n, 3)*5/16;
    SET l_a2=POW(l_n, 2)*13/48-POW(l_n, 3)*3/5;
    SET l_a3=POW(l_n, 3)*61/240;
    SET l_b1=l_n/2-POW(l_n, 2)*2/3+POW(l_n, 3)*37/96;
    SET l_b2=POW(l_n, 2)/48+POW(l_n, 3)/15;
    SET l_b3=POW(l_n, 3)*17/480;
    SET l_d1=l_n*2-POW(l_n, 2)*2/3-POW(l_n, 3)*2;
    SET l_d2=POW(l_n, 2)*7/3-POW(l_n, 3)*8/5;
    SET l_d3=POW(l_n, 3)*56/15;
    
    /*Se calculan los valores intermedios*/
    SET l_long0=in_zona*6-183;
    SET l_e=(l_y-l_y0)/(l_k0*l_aa);
    SET l_nn=(l_x-l_x0)/(l_k0*l_aa);
    SET l_ep=l_e - l_b1*SIN(2*l_e)*fu_cosh(2*l_nn) - l_b2*SIN(4*l_e)*fu_cosh(4*l_nn) - l_b3*SIN(6*l_e)*fu_cosh(6*l_nn);
    SET l_np=l_nn - l_b1*COS(2*l_e)*fu_sinh(2*l_nn) - l_b2*COS(4*l_e)*fu_sinh(4*l_nn) - l_b3*COS(6*l_e)*fu_sinh(6*l_nn);
    SET l_rp=1 - 2*l_b1*COS(2*l_e)*fu_cosh(2*l_nn) - 4*l_b2*COS(4*l_e)*fu_cosh(4*l_nn) - 6*l_b3*COS(6*l_e)*fu_cosh(6*l_nn);
    SET l_tp=2*l_b1*SIN(2*l_e)*fu_sinh(2*l_nn) + 4*l_b2*SIN(4*l_e)*fu_sinh(4*l_nn) + 6*l_b3*SIN(6*l_e)*fu_sinh(6*l_nn);
    SET l_xx=ASIN(SIN(l_ep)/fu_cosh(l_np));
    
    /*Se calculan los valores de las coordenadas*/
    SET l_latitud=DEGREES(l_xx + l_d1*SIN(2*l_xx) + l_d2*SIN(4*l_xx) + l_d3*SIN(6*l_xx));
    SET l_longitud=l_long0+DEGREES(ATAN(fu_sinh(l_np)/COS(l_ep)));
    
    IF UPPER(in_tipo)='LAT' THEN
        SET l_resultado=l_latitud;
    ELSEIF UPPER(in_tipo)='LON' THEN
        SET l_resultado=l_longitud;
    END IF;
    
    RETURN l_resultado;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_borrar_evento` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_borrar_evento`(IN in_id_evento BIGINT(20),
                                    OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_cantidad_aux INT(10);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_resultado=-2;
    
    START TRANSACTION;
    
    /*Se verifica si hay resultados de Función K asociados*/
    SELECT COUNT(*) INTO l_cantidad_aux
    FROM knet_resultados
    WHERE id_evento=in_id_evento;
    
    IF l_cantidad_aux=0 THEN
        /*Se verifica si hay resultados de NetKDE asociados*/
        SELECT COUNT(*) INTO l_cantidad_aux
        FROM netkde_resultados
        WHERE id_evento=in_id_evento;
        
        IF l_cantidad_aux=0 THEN
            /*Se borran los registro*/
            DELETE FROM eventos_puntos_atributos
            WHERE id_evento=in_id_evento;
            
            DELETE FROM eventos_puntos
            WHERE id_evento=in_id_evento;
            
            DELETE FROM eventos_atributos
            WHERE id_evento=in_id_evento;
            
            DELETE FROM eventos
            WHERE id_evento=in_id_evento;
            
            SET out_resultado=1;
        ELSE
            SET out_resultado=-4;
        END IF;
    ELSE
        SET out_resultado=-3;
    END IF;
    
    COMMIT;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_borrar_knet_resultado` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_borrar_knet_resultado`(IN in_id_knet BIGINT(20),
                                            OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_resultado=-2;
    
    START TRANSACTION;
    
    /*Se borran los valores*/
    DELETE FROM knet_valores
    WHERE id_knet=in_id_knet;
    
    DELETE FROM knet_resultados
    WHERE id_knet=in_id_knet;
    
    COMMIT;
    
    SET out_resultado=1;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_borrar_netkde_resultado` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_borrar_netkde_resultado`(IN in_id_netkde BIGINT(20),
                                              OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_resultado=-2;
    
    START TRANSACTION;
    
    /*Se borran los valores*/
    DELETE FROM netkde_lixels_puntos
    WHERE id_netkde=in_id_netkde;
    
    DELETE FROM netkde_lixels_det
    WHERE id_netkde=in_id_netkde;
    
    DELETE FROM netkde_lixels
    WHERE id_netkde=in_id_netkde;
    
    DELETE FROM netkde_resultados
    WHERE id_netkde=in_id_netkde;
    
    COMMIT;
    
    SET out_resultado=1;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_borrar_red` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_borrar_red`(IN in_id_red BIGINT(20),
                                 OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_cantidad_aux INT(10);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_resultado=-2;
    
    START TRANSACTION;
    
    /*Se verifica si hay eventos asociados*/
    SELECT COUNT(*) INTO l_cantidad_aux
    FROM eventos
    WHERE id_red=in_id_red;
    
    IF l_cantidad_aux=0 THEN
        /*Se borran los temporales*/
        DELETE FROM redes_lineas_det
        WHERE id_red=in_id_red;
        
        DELETE FROM redes_lineas_atributos
        WHERE id_red=in_id_red;
        
        DELETE FROM redes_lineas
        WHERE id_red=in_id_red;
        
        DELETE FROM redes_atributos
        WHERE id_red=in_id_red;
        
        DELETE FROM redes
        WHERE id_red=in_id_red;
        
        SET out_resultado=1;
    ELSE
        SET out_resultado=-3;
    END IF;
        
    COMMIT;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_buscar_linea_det_cercana` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_buscar_linea_det_cercana`(IN in_id_red BIGINT(20),
                                               IN in_dist_proy DOUBLE,
                                               IN in_latitud_punto DOUBLE,
                                               IN in_longitud_punto DOUBLE,
                                               in in_factor_metros DOUBLE,
                                               IN in_ind_grados INT(1),
                                               OUT out_id_linea BIGINT(20),
                                               OUT out_num_punto BIGINT(20),
                                               OUT out_latitud DOUBLE,
                                               OUT out_longitud DOUBLE)
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_linea, l_id_linea_act BIGINT(20);
    DECLARE l_num_punto, l_num_punto_ini, l_num_punto_fin BIGINT(20);
    DECLARE l_latitud, l_longitud DOUBLE;
    DECLARE l_latitud_ini, l_longitud_ini DOUBLE;
    DECLARE l_latitud_fin, l_longitud_fin DOUBLE;
    DECLARE l_latitud_proy, l_longitud_proy DOUBLE;
    DECLARE l_x_punto, l_y_punto, l_x_ini, l_y_ini, l_x_fin, l_y_fin, l_x_proy, l_y_proy DOUBLE;
    DECLARE l_pendiente DOUBLE;
    DECLARE l_cruce_y DOUBLE;
    DECLARE l_distancia, l_distancia_act, l_distancia_ini, l_distancia_fin DOUBLE;
    DECLARE l_ind_fin INT;
    
    DECLARE cur_lineas_det CURSOR FOR
        SELECT id_linea, num_punto_ini, latitud_ini, longitud_ini, num_punto_fin, latitud_fin, longitud_fin, distancia
        FROM (
            SELECT LD1.id_linea, LD1.num_punto AS num_punto_ini, LD1.latitud AS latitud_ini, LD1.longitud AS longitud_ini,
            LD2.num_punto AS num_punto_fin, LD2.latitud AS latitud_fin, LD2.longitud AS longitud_fin,
            fu_calcular_distancia_punto_linea(in_latitud_punto, in_longitud_punto, LD1.latitud, LD1.longitud, LD2.latitud, LD2.longitud, in_factor_metros, in_ind_grados) AS distancia
            FROM redes_lineas_det LD1
            INNER JOIN redes_lineas_det LD2 ON LD1.id_red=LD2.id_red AND LD1.id_linea=LD2.id_linea
            INNER JOIN redes_lineas RL ON LD1.id_red=RL.id_red AND LD1.id_linea=RL.id_linea
            WHERE LD1.id_red=in_id_red
            AND LD2.num_punto=LD1.num_punto+1
            AND RL.largo_linea>0
        ) T
        WHERE T.distancia<=in_dist_proy
        ORDER by distancia, id_linea, num_punto_ini;
    
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET l_ind_fin=1;
    
    SET out_id_linea=-1;
    
    /*Se recorren las líneas y se calculan las distancias al punto*/
    SET l_id_linea=NULL, l_num_punto=NULL, l_latitud=NULL, l_longitud=NULL, l_distancia=in_dist_proy+1;
    SET l_ind_fin=0;
    OPEN cur_lineas_det;
    
    REPEAT
        FETCH cur_lineas_det INTO l_id_linea_act, l_num_punto_ini, l_latitud_ini, l_longitud_ini,
            l_num_punto_fin, l_latitud_fin, l_longitud_fin, l_distancia_act;
        
        IF NOT l_ind_fin THEN
            IF l_distancia_act=0 THEN
                /*Si la distancia es cero, no hay que proyectar el punto*/
                SET l_latitud_proy=in_latitud_punto;
                SET l_longitud_proy=in_longitud_punto;
            ELSE
                /*Se buscan las coordenadas de la proyección*/
                IF l_longitud_ini=l_longitud_fin THEN
                    /*Se trata de una línea vertical*/
                    SET l_latitud_proy=in_latitud_punto, l_longitud_proy=l_longitud_ini;
                ELSEIF l_latitud_ini=l_latitud_fin THEN
                    /*Se trata de una línea horizontal*/
                    SET l_latitud_proy=l_latitud_ini, l_longitud_proy=in_longitud_punto;
                ELSE
                    IF in_ind_grados=1 THEN
                        /*Se deben convertir las coordenadas geográficas a coordenadas cartesianas, se toma como punto de origen el punto recibido*/
                        SET l_x_punto=0, l_y_punto=0;
                        SET l_x_ini=fu_transformar_longitud_plana(l_longitud_ini, in_latitud_punto, in_longitud_punto, in_factor_metros);
                        SET l_y_ini=fu_transformar_latitud_plana(l_latitud_ini, in_latitud_punto, in_longitud_punto, in_factor_metros);
                        SET l_x_fin=fu_transformar_longitud_plana(l_longitud_fin, in_latitud_punto, in_longitud_punto, in_factor_metros);
                        SET l_y_fin=fu_transformar_latitud_plana(l_latitud_fin, in_latitud_punto, in_longitud_punto, in_factor_metros);
                    ELSE
                        SET l_x_punto=in_longitud_punto;
                        SET l_y_punto=in_latitud_punto;
                        SET l_x_ini=l_longitud_ini;
                        SET l_y_ini=l_latitud_ini;
                        SET l_x_fin=l_longitud_fin;
                        SET l_y_fin=l_latitud_fin;
                    END IF;
                    
                    SET l_pendiente=(l_y_fin-l_y_ini)/(l_x_fin-l_x_ini);
                    SET l_cruce_y=l_y_ini-l_pendiente*l_x_ini;
                    
                    SET l_x_proy=(l_x_punto+l_pendiente*l_y_punto-l_pendiente*l_cruce_y)/(POW(l_pendiente, 2) + 1);
                    SET l_y_proy=(l_pendiente*l_x_punto+POW(l_pendiente, 2)*l_y_punto+l_cruce_y)/(POW(l_pendiente, 2) + 1);
                    
                    IF in_ind_grados=1 THEN
                        SET l_latitud_proy=l_latitud_ini+((l_y_proy-l_y_ini)/(l_y_fin-l_y_ini))*(l_latitud_fin-l_latitud_ini);
                        SET l_longitud_proy=l_longitud_ini+((l_x_proy-l_x_ini)/(l_x_fin-l_x_ini))*(l_longitud_fin-l_longitud_ini);
                    ELSE
                        SET l_latitud_proy=l_y_proy;
                        SET l_longitud_proy=l_x_proy;
                    END IF;
                END IF;
            END IF;
            
            /*Si la distancia actual es menor que la distancia menor hallada*/
            IF l_distancia_act<l_distancia THEN
                /*Se verifica que la proyección se encuentre sobre la línea*/
                IF ((l_latitud_proy>=l_latitud_ini AND l_latitud_proy<=l_latitud_fin) OR
                        (l_latitud_proy>=l_latitud_fin AND l_latitud_proy<=l_latitud_ini)) AND
                        ((l_longitud_proy>=l_longitud_ini AND l_longitud_proy<=l_longitud_fin) OR
                        (l_longitud_proy>=l_longitud_fin AND l_longitud_proy<=l_longitud_ini)) THEN
                    /*Está adentro, es la proyección de menor distancia*/
                    SET l_id_linea=l_id_linea_act;
                    SEt l_num_punto=l_num_punto_ini;
                    SET l_latitud=l_latitud_proy;
                    SET l_longitud=l_longitud_proy;
                    SET l_distancia=l_distancia_act;
                    
                    SET l_ind_fin=1;
                ELSE
                    /*Se hallan las distancias a los extremos, se escoge la menor
                    y se comprueba si está dentro del rango y si es menor que la distancia hallada*/
                    SET l_distancia_ini=SQRT(POW(l_x_proy-l_x_ini, 2)+POW(l_y_proy-l_y_ini, 2));
                    SET l_distancia_fin=SQRT(POW(l_x_proy-l_x_fin, 2)+POW(l_y_proy-l_y_fin, 2));
                    
                    IF l_distancia_ini<=l_distancia_fin AND l_distancia_ini<l_distancia AND l_distancia_ini<=in_dist_proy THEN
                        SET l_id_linea=l_id_linea_act;
                        SET l_num_punto=l_num_punto_ini;
                        SET l_latitud=l_latitud_ini;
                        SET l_longitud=l_longitud_ini;
                        SET l_distancia=l_distancia_ini;
                    ELSEIF l_distancia_fin<l_distancia_ini AND l_distancia_fin<l_distancia AND l_distancia_fin<=in_dist_proy THEN
                        SET l_id_linea=l_id_linea_act;
                        SET l_num_punto=l_num_punto_ini;
                        SET l_latitud=l_latitud_fin;
                        SET l_longitud=l_longitud_fin;
                        SET l_distancia=l_distancia_fin;
                    END IF;
                END IF;
            ELSE
                SET l_ind_fin=1;
            END IF;
        END IF;
    UNTIL l_ind_fin END REPEAT;
    
    CLOSE cur_lineas_det;
    
    SET out_id_linea=l_id_linea;
    SET out_num_punto=l_num_punto;
    SET out_latitud=l_latitud;
    SET out_longitud=l_longitud;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_calcular_largos_lineas_red` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_calcular_largos_lineas_red`(IN in_id_red BIGINT(20),
                                                 IN in_autocommit INT(1),
                                                 OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_factor_metros DOUBLE;
    DECLARE l_ind_grados INT(1);
    DECLARE l_id_linea BIGINT(20);
    DECLARE l_id_linea_ant BIGINT(20);
    DECLARE l_num_punto BIGINT(20);
    DECLARE l_latitud_ini DOUBLE;
    DECLARE l_longitud_ini DOUBLE;
    DECLARE l_latitud_fin DOUBLE;
    DECLARE l_longitud_fin DOUBLE;
    DECLARE l_largo_linea DOUBLE;
    DECLARE l_largo_acumulado DOUBLE;
    DECLARE l_largo_segmento DOUBLE;
    DECLARE l_ind_fin INT;
    
    DECLARE cur_lineas CURSOR FOR
        SELECT id_linea, num_punto, latitud, longitud
        FROM redes_lineas_det
        WHERE id_red=in_id_red
        ORDER BY id_linea, num_punto;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET l_ind_fin=1;
    
    SET out_resultado=-1;
    
    IF in_autocommit=1 THEN
        START TRANSACTION;
    END IF;
    
    /*Se obtienen los datos de la unidad de medida de la red*/
    SELECT U.factor_metros, U.ind_grados INTO l_factor_metros, l_ind_grados
    FROM redes R
    INNER JOIN sistemas_coordenadas SC ON R.id_sistema=SC.id_sistema
    INNER JOIN unidades_medida U ON SC.id_unidad=U.id_unidad
    WHERE R.id_red=in_id_red;
    
    /*Se recorren los puntos y se calculan las distancias*/
    SET l_ind_fin=0, l_id_linea_ant=0, l_largo_linea=0;
    OPEN cur_lineas;
    
    SET l_largo_acumulado=0;
    REPEAT
        FETCH cur_lineas INTO l_id_linea, l_num_punto, l_latitud_ini, l_longitud_ini;
        
        IF NOT l_ind_fin THEN
            IF l_id_linea<>l_id_linea_ant THEN
                /*Cambio de línea*/
                SET l_largo_linea=0;
            ELSE
                /*Se calcula la longitud del segmento y se agrega al calculo acumulado*/
                SET l_largo_segmento=fu_calcular_largo_linea_puntos(l_latitud_ini, l_longitud_ini, l_latitud_fin, l_longitud_fin, l_factor_metros, l_ind_grados);
                SET l_largo_linea=l_largo_linea+l_largo_segmento;
                SET l_largo_acumulado=l_largo_acumulado+l_largo_segmento;
                
                /*Se actualiza la longitud del segmento*/
                UPDATE redes_lineas_det
                SET largo_segmento=l_largo_segmento
                WHERE id_red=in_id_red
                AND id_linea=l_id_linea
                AND num_punto=l_num_punto;
                
                /*Se actualiza la longitud de la línea*/
                UPDATE redes_lineas
                SET largo_linea=l_largo_linea,
                largo_acumulado=l_largo_acumulado
                WHERE id_red=in_id_red
                AND id_linea=l_id_linea;
            END IF;
            
            SET l_id_linea_ant=l_id_linea, l_latitud_fin=l_latitud_ini, l_longitud_fin=l_longitud_ini;
        END IF;
    UNTIL l_ind_fin END REPEAT;
    
    CLOSE cur_lineas;
    
    IF in_autocommit=1 THEN
        COMMIT;
    END IF;
    
    SET out_resultado=1;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_crear_evento` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_crear_evento`(IN in_id_evento BIGINT(20),
                                   IN in_nombre_atributo_fecha VARCHAR(50),
                                   IN in_nombre_atributo_hora VARCHAR(50),
                                   OUT out_id_evento BIGINT(20))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_evento BIGINT(20);
    DECLARE l_id_atributo_fecha BIGINT(20);
    DECLARE l_id_atributo_hora BIGINT(20);
    DECLARE l_cantidad_aux INT(10);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_id_evento=-1;
    
    START TRANSACTION;
    
    /*Se busca el id del atributo de fecha*/
    SET l_id_atributo_fecha=NULL;
    IF in_nombre_atributo_fecha IS NOT NULL THEN
        SELECT COUNT(*) INTO l_cantidad_aux
        FROM tmp_eventos_atributos
        WHERE id_evento=in_id_evento
        AND nombre_atributo=in_nombre_atributo_fecha;
        
        IF l_cantidad_aux>0 THEN
            SELECT id_atributo INTO l_id_atributo_fecha
            FROM tmp_eventos_atributos
            WHERE id_evento=in_id_evento
            AND nombre_atributo=in_nombre_atributo_fecha
            LIMIT 1;
        END IF;
    END IF;
    
    /*Se busca el id del atributo de hora*/
    SET l_id_atributo_hora=NULL;
    IF in_nombre_atributo_hora IS NOT NULL THEN
        SELECT COUNT(*) INTO l_cantidad_aux
        FROM tmp_eventos_atributos
        WHERE id_evento=in_id_evento
        AND nombre_atributo=in_nombre_atributo_hora;
        
        IF l_cantidad_aux>0 THEN
            SELECT id_atributo INTO l_id_atributo_hora
            FROM tmp_eventos_atributos
            WHERE id_evento=in_id_evento
            AND nombre_atributo=in_nombre_atributo_hora
            LIMIT 1;
        END IF;
    END IF;
    
    /*Se crea el registro del evento*/
    INSERT INTO eventos
    (desc_evento, id_red, fecha_crea, id_atributo_fecha, id_atributo_hora)
    SELECT desc_evento, id_red, NOW(), l_id_atributo_fecha, l_id_atributo_hora
    FROM tmp_eventos
    WHERE id_evento=in_id_evento;
    
    /*Se obtiene el id del registro insertado*/
    SELECT LAST_INSERT_ID() INTO l_id_evento;
    
    /*Se crean los registros de atributos*/
    INSERT INTO eventos_atributos
    (id_evento, id_atributo, nombre_atributo, tipo_atributo)
    SELECT l_id_evento, id_atributo, nombre_atributo, tipo_atributo
    FROM tmp_eventos_atributos
    WHERE id_evento=in_id_evento;
    
    /*Se crean los registros de puntos*/
    INSERT INTO eventos_puntos
    (id_evento, id_punto, fecha_punto, latitud, longitud)
    SELECT l_id_evento, id_punto, fecha_punto, latitud, longitud
    FROM tmp_eventos_puntos
    WHERE id_evento=in_id_evento;
    
    /*Se crean los registros de atributos de puntos*/
    INSERT INTO eventos_puntos_atributos
    (id_evento, id_punto, id_atributo, valor_num, valor_tex)
    SELECT l_id_evento, id_punto, id_atributo, valor_num, valor_tex
    FROM tmp_eventos_puntos_atributos
    WHERE id_evento=in_id_evento;
    
    /*Se borran los temporales*/
    DELETE FROM tmp_eventos_puntos_atributos
    WHERE id_evento=in_id_evento;
    
    DELETE FROM tmp_eventos_puntos
    WHERE id_evento=in_id_evento;
    
    DELETE FROM tmp_eventos_atributos
    WHERE id_evento=in_id_evento;
    
    DELETE FROM tmp_eventos
    WHERE id_evento=in_id_evento;
    
    COMMIT;
    
    SET out_id_evento=l_id_evento;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_crear_knet_resultado` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_crear_knet_resultado`(IN in_id_knet BIGINT(20),
                                           OUT out_id_knet BIGINT(20))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_knet BIGINT(20);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_id_knet=-1;
    
    START TRANSACTION;
    
    /*Se crea el registro de resultado con base en el resultado temporal*/
    INSERT INTO knet_resultados
    (id_evento, distancia_ini, distancia_fin, incremento_dist, cant_puntos, cant_aleatorios, fecha_resultado, filtros_resultado)
    SELECT id_evento, distancia_ini, distancia_fin, incremento_dist, cant_puntos, cant_aleatorios, NOW(), filtros_resultado
    FROM tmp_knet_resultados
    WHERE id_knet=in_id_knet;
    
    /*Se obtiene el id del registro insertado*/
    SELECT LAST_INSERT_ID() INTO l_id_knet;
    
    /*Se crean los registros de valores*/
    INSERT INTO knet_valores
    (id_knet, distancia_knet, valor, limite_min, limite_max)
    SELECT l_id_knet, distancia_knet, valor, limite_min, limite_max
    FROM tmp_knet_valores
    WHERE id_knet=in_id_knet;
    
    /*Se borran los valores temporales*/
    DELETE FROM tmp_knet_valores
    WHERE id_knet=in_id_knet;
    
    DELETE FROM tmp_knet_resultados
    WHERE id_knet=in_id_knet;
    
    COMMIT;
    
    SET out_id_knet=l_id_knet;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_crear_netkde_resultado` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_crear_netkde_resultado`(IN in_id_netkde BIGINT(20),
                                             OUT out_id_netkde BIGINT(20))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_netkde BIGINT(20);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_id_netkde=-1;
    
    START TRANSACTION;
    
    /*Se crea el registro de resultado con base en el resultado temporal*/
    INSERT INTO netkde_resultados
    (id_evento, ancho_banda, largo_lixel, id_funcion, cant_puntos, fecha_resultado, filtros_resultado)
    SELECT id_evento, ancho_banda, largo_lixel, id_funcion, cant_puntos, NOW(), filtros_resultado
    FROM tmp_netkde_resultados
    WHERE id_netkde=in_id_netkde;
    
    /*Se obtiene el id del registro insertado*/
    SELECT LAST_INSERT_ID() INTO l_id_netkde;
    
    /*Se crean los registros de lixels*/
    INSERT INTO netkde_lixels
    (id_netkde, id_lixel, id_red, id_linea, num_punto,
    lat_lxcenter, lon_lxcenter, largo_lixel, cantidad_puntos, densidad_lixel)
    SELECT l_id_netkde, id_lixel, id_red, id_linea, num_punto,
    lat_lxcenter, lon_lxcenter, largo_lixel, cantidad_puntos, densidad_lixel
    FROM tmp_netkde_lixels
    WHERE id_netkde=in_id_netkde;
    
    /*Se crean los registros de detalle de lixels*/
    INSERT INTO netkde_lixels_det
    (id_netkde, id_lixel, num_punto, latitud, longitud, largo_segmento)
    SELECT l_id_netkde, id_lixel, num_punto, latitud, longitud, largo_segmento
    FROM tmp_netkde_lixels_det
    WHERE id_netkde=in_id_netkde;
    
    /*Se crean los registros de puntos (eventos) asociados a cada lixel*/
    INSERT INTO netkde_lixels_puntos
    (id_netkde, id_lixel, id_evento, id_punto)
    SELECT l_id_netkde, id_lixel, id_evento, id_punto
    FROM tmp_netkde_lixels_puntos
    WHERE id_netkde=in_id_netkde;
    
    /*Se borran los valores temporales*/
    DELETE FROM tmp_netkde_lixels_puntos
    WHERE id_netkde=in_id_netkde;
    
    DELETE FROM tmp_netkde_lixels_det
    WHERE id_netkde=in_id_netkde;
    
    DELETE FROM tmp_netkde_lixels
    WHERE id_netkde=in_id_netkde;
    
    DELETE FROM tmp_netkde_resultados
    WHERE id_netkde=in_id_netkde;
    
    COMMIT;
    
    SET out_id_netkde=l_id_netkde;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_crear_red` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_crear_red`(IN in_id_red BIGINT(20),
                                OUT out_id_red BIGINT(20))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_red BIGINT(20);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_id_red=-1;
    
    START TRANSACTION;
    
    /*Se crea el registro de red*/
    INSERT INTO redes
    (desc_red, id_sistema, fecha_crea)
    SELECT desc_red, id_sistema, NOW()
    FROM tmp_redes
    WHERE id_red=in_id_red;
    
    /*Se obtiene el id del registro insertado*/
    SELECT LAST_INSERT_ID() INTO l_id_red;
    
    /*Se crean los registros de atributos*/
    INSERT INTO redes_atributos
    (id_red, id_atributo, nombre_atributo, tipo_atributo)
    SELECT l_id_red, id_atributo, nombre_atributo, tipo_atributo
    FROM tmp_redes_atributos
    WHERE id_red=in_id_red;
    
    /*Se crean los registros de líneas*/
    INSERT INTO redes_lineas
    (id_red, id_linea)
    SELECT l_id_red, id_linea
    FROM tmp_redes_lineas
    WHERE id_red=in_id_red;
    
    /*Se crean los registros de atributos de líneas*/
    INSERT INTO redes_lineas_atributos
    (id_red, id_linea, id_atributo, valor_num, valor_tex)
    SELECT l_id_red, id_linea, id_atributo, valor_num, valor_tex
    FroM tmp_redes_lineas_atributos
    WHERE id_red=in_id_red;
    
    /*Se crean los registros de detalle de líneas*/
    INSERT INTO redes_lineas_det
    (id_red, id_linea, num_punto, latitud, longitud)
    SELECT l_id_red, id_linea, num_punto, latitud, longitud
    FROM tmp_redes_lineas_det
    WHERE id_red=in_id_red;
    
    /*Se calculan las longitudes de las líneas*/
    CALL pa_calcular_largos_lineas_red(l_id_red, 0, @resultado);
    
    IF @resultado>0 THEN
        /*Se borran los temporales*/
        DELETE FROM tmp_redes_lineas_det
        WHERE id_red=in_id_red;
        
        DELETE FROM tmp_redes_lineas_atributos
        WHERE id_red=in_id_red;
        
        DELETE FROM tmp_redes_lineas
        WHERE id_red=in_id_red;
        
        DELETE FROM tmp_redes_atributos
        WHERE id_red=in_id_red;
        
        DELETE FROM tmp_redes
        WHERE id_red=in_id_red;
        
        COMMIT;
        SET out_id_red=l_id_red;
    ELSE
        ROLLBACK;
        SET out_id_red=-2;
    END IF;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_crear_tmp_evento` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_crear_tmp_evento`(IN in_desc_evento VARCHAR(200),
                                       IN in_id_red BIGINT(20),
                                       OUT out_id_evento BIGINT(20))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_evento BIGINT(20);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_id_evento=-1;
    
    /*Se crea el registro*/
    START TRANSACTION;
    
    INSERT INTO tmp_eventos
    (desc_evento, id_red)
    VALUES (in_desc_evento, in_id_red);
    
    /*Se obtiene el id del registro insertado*/
    SELECT LAST_INSERT_ID() INTO l_id_evento;
    
    COMMIT;
    
    SET out_id_evento=l_id_evento;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_crear_tmp_knet_resultado` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_crear_tmp_knet_resultado`(IN in_id_evento BIGINT(20),
                                               IN in_distancia_ini DOUBLE,
                                               IN in_distancia_fin DOUBLE,
                                               IN in_incremento_dist DOUBLE,
                                               IN in_cant_puntos INT(10),
                                               IN in_cant_aleatorios INT(10),
                                               IN in_filtros_resultado TEXT,
                                               OUT out_id_knet BIGINT(20))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_knet BIGINT(20);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_id_knet=-1;
    
    /*Se crea el registro*/
    START TRANSACTION;
    
    INSERT INTO tmp_knet_resultados
    (id_evento, distancia_ini, distancia_fin, incremento_dist, cant_puntos, cant_aleatorios, filtros_resultado)
    VALUES (in_id_evento, in_distancia_ini, in_distancia_fin, in_incremento_dist, in_cant_puntos, in_cant_aleatorios, in_filtros_resultado);
    
    /*Se obtiene el id del registro insertado*/
    SELECT LAST_INSERT_ID() INTO l_id_knet;
    
    COMMIT;
    
    SET out_id_knet=l_id_knet;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_crear_tmp_knet_valor` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_crear_tmp_knet_valor`(IN in_id_knet BIGINT(20),
                                           IN in_distancia_knet DOUBLE,
                                           IN in_valor DOUBLE,
                                           IN in_limite_min DOUBLE,
                                           IN in_limite_max DOUBLE,
                                           OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_resultado=-1;
    
    /*Se crea el registro*/
    START TRANSACTION;
    
    INSERT INTO tmp_knet_valores
    (id_knet, distancia_knet, valor, limite_min, limite_max)
    VALUES (in_id_knet, in_distancia_knet, in_valor, in_limite_min, in_limite_max);
    
    COMMIT;
    
    SET out_resultado=1;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_crear_tmp_netkde_resultado` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_crear_tmp_netkde_resultado`(IN in_id_evento BIGINT(20),
                                                 IN in_ancho_banda DOUBLE,
                                                 IN in_largo_lixel DOUBLE,
                                                 IN in_id_funcion INT(10),
                                                 IN in_cant_puntos INT(10),
                                                 IN in_filtros_resultado TEXT,
                                                 OUT out_id_netkde BIGINT(20))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_netkde BIGINT(20);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_id_netkde=-1;
    
    /*Se crea el registro*/
    START TRANSACTION;
    
    INSERT INTO tmp_netkde_resultados
    (id_evento, ancho_banda, largo_lixel, id_funcion, cant_puntos, filtros_resultado)
    VALUES (in_id_evento, in_ancho_banda, in_largo_lixel, in_id_funcion, in_cant_puntos, in_filtros_resultado);
    
    /*Se obtiene el id del registro insertado*/
    SELECT LAST_INSERT_ID() INTO l_id_netkde;
    
    COMMIT;
    
    SET out_id_netkde=l_id_netkde;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_crear_tmp_red` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_crear_tmp_red`(IN in_desc_red VARCHAR(200),
                                    IN in_id_sistema BIGINT(20),
                                    OUT out_id_red BIGINT(20))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_red BIGINT(20);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_id_red=-1;
    
    /*Se crea el registro*/
    START TRANSACTION;
    
    INSERT INTO tmp_redes
    (desc_red, id_sistema)
    VALUES (in_desc_red, in_id_sistema);
    
    /*Se obtiene el id del registro insertado*/
    SELECT LAST_INSERT_ID() INTO l_id_red;
    
    COMMIT;
    
    SET out_id_red=l_id_red;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_editar_red_cierre_nodos` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_editar_red_cierre_nodos`(IN in_id_red BIGINT(20),
                                              IN in_ind_cierre_nodos INT(1),
                                              IN in_dist_cierre_nodos DOUBLE,
                                              IN in_cant_cierres INT(10),
                                              OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_linea BIGINT(20);
    DECLARE l_num_punto BIGINT(20);
    DECLARE l_id_linea_ant BIGINT(20);
    DECLARE l_num_punto_cor BIGINT(20);
    DECLARE l_ind_fin INT;
    
    DECLARE cur_lineas_det CURSOR FOR
        SELECT id_linea, num_punto
        FROM redes_lineas_det
        WHERE id_red=in_id_red
        ORDER BY id_linea, num_punto;
    
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET l_ind_fin=1;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_resultado=-1;
    
    START TRANSACTION;
    
    UPDATE redes
    SET ind_cierre_nodos=in_ind_cierre_nodos,
    dist_cierre_nodos=in_dist_cierre_nodos
    WHERE id_red=in_id_red;
    
    /*Se borran los nodos no iniciales de longitud cero*/
    DELETE FROM redes_lineas_det
    WHERE id_red=in_id_red
    AND num_punto<>1
    AND largo_segmento=0;
    
    /*Se renumeran los puntos*/
    SET l_id_linea_ant=-1;
    SET l_num_punto_cor=1;
    SET l_ind_fin=0;
    OPEN cur_lineas_det;
    
    REPEAT
        FETCH cur_lineas_det INTO l_id_linea, l_num_punto;
        
        IF NOT l_ind_fin THEN
            IF l_id_linea<>l_id_linea_ant THEN
                SET l_num_punto_cor=1;
            END IF;
            
            IF l_num_punto<>l_num_punto_cor THEN
                /*Se actualiza el identificador del punto*/
                UPDATE redes_lineas_det
                SET num_punto=l_num_punto_cor
                WHERE id_red=in_id_red
                AND id_linea=l_id_linea
                AND num_punto=l_num_punto;
            END IF;
            
            SET l_id_linea_ant=l_id_linea;
            SET l_num_punto_cor=l_num_punto_cor+1;
        END IF;
    UNTIL l_ind_fin END REPEAT;
    
    CLOSE cur_lineas_det;
    
    /*Si se marcó como cerrada la red y hubo cambios en algunos nodos, se marcan como no proyectados los eventos asociados*/
    IF in_ind_cierre_nodos=1 AND in_cant_cierres>0 THEN
        UPDATE eventos
        SET ind_proy=0,
        dist_proy=NULL
        WHERE id_red=in_id_red;
    END IF;
    
    COMMIT;
    
    SET out_resultado=1;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_editar_red_linea_det_coordenadas` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_editar_red_linea_det_coordenadas`(IN in_id_red BIGINT(20),
                                                       IN in_id_linea BIGINT(20),
                                                       IN in_num_punto BIGINT(20),
                                                       IN in_latitud DOUBLE,
                                                       IN in_longitud DOUBLE,
                                                       OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_resultado=-1;
    
    START TRANSACTION;
    
    UPDATE redes_lineas_det
    SET latitud=in_latitud,
    longitud=in_longitud
    WHERE id_red=in_id_red
    AND id_linea=in_id_linea
    AND num_punto=in_num_punto;
    
    
    COMMIT;
    
    SET out_resultado=1;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_editar_tmp_evento_punto` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_editar_tmp_evento_punto`(IN in_id_evento BIGINT(20),
                                              IN in_id_punto BIGINT(20),
                                              IN in_fecha_punto VARCHAR(30),
                                              IN in_latitud DOUBLE,
                                              IN in_longitud DOUBLE,
                                              OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_fecha_punto DATETIME;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_resultado=-1;
    
    /*Se convierte la hora de VARCHAR a DATETIME*/
    SET l_fecha_punto=STR_TO_DATE(in_fecha_punto, '%d/%m/%Y %l:%i:%s %p');
    
    /*Se crea el registro*/
    START TRANSACTION;
    
    UPDATE tmp_eventos_puntos
    SET fecha_punto=l_fecha_punto,
    latitud=in_latitud,
    longitud=in_longitud
    WHERE id_evento=in_id_evento
    AND id_punto=in_id_punto;
    
    COMMIT;
    
    SET out_resultado=1;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_generar_puntos_aleatorios` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_generar_puntos_aleatorios`(IN in_id_evento BIGINT(20),
                                                IN in_id_red_tmp BIGINT(20),
                                                OUT out_id_evento BIGINT(20))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_red BIGINT(20);
    DECLARE l_id_evento_tmp BIGINT(20);
    DECLARE l_id_linea BIGINT(20);
    DECLARE l_largo_red DOUBLE;
    DECLARE l_cant_puntos BIGINT(20);
    DECLARE l_id_punto BIGINT(20);
    DECLARE l_num_punto_ini BIGINT(20);
    DECLARE l_num_punto_fin BIGINT(20);
    DECLARE l_distancia_aux DOUBLE;
    DECLARE l_factor_aux DOUBLE;
    DECLARE l_largo_acumulado DOUBLE;
    DECLARE l_largo_acumulado_act DOUBLE;
    DECLARE l_largo_acumulado_ini DOUBLE;
    DECLARE l_largo_acumulado_fin DOUBLE;
    DECLARE l_latitud DOUBLE;
    DECLARE l_longitud DOUBLE;
    DECLARE l_latitud_ini DOUBLE;
    DECLARE l_longitud_ini DOUBLE;
    DECLARE l_latitud_fin DOUBLE;
    DECLARE l_longitud_fin DOUBLE;
    DECLARE l_cantidad_aux INT(10);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_id_evento=-1;
    
    START TRANSACTION;
    
    /*Se cargan los datos de la red*/
    SELECT E.id_red, RL.largo_red, COUNT(*) INTO l_id_red, l_largo_red, l_cant_puntos
    FROM eventos E
    INNER JOIN eventos_puntos EP ON E.id_evento=EP.id_evento
    INNER JOIN redes R ON E.id_red=R.id_red
    INNER JOIN (
        SELECT id_red, MAX(largo_acumulado) AS largo_red
        FROM redes_lineas
        GROUP BY id_red
    ) RL ON E.id_red=RL.id_red
    WHERE E.id_evento=in_id_evento
    GROUP BY E.id_red;
    
    /*Se crea el evento temporal sobre el que se crearán los puntos aleatorios*/
    INSERT INTO tmp_eventos (desc_evento, id_red)
    VALUES ('Evento temporal para puntos aleatorios de la Función K para Redes', in_id_red_tmp);
    
    /*Se obtiene el identificador del evento temporal*/
    SELECT LAST_INSERT_ID() INTO l_id_evento_tmp;
    
    /*Se generan los puntos*/
    SET l_id_punto=1;
    WHILE l_id_punto<=l_cant_puntos DO
        /*Se genera un valor aleatorio dentro de la longitud total de la red*/
        SET l_distancia_aux=RAND()*l_largo_red;
        
        /*Se busca la línea sobre la que se ubicará el punto*/
        SELECT MIN(L.id_linea) INTO l_id_linea
        FROM redes_lineas L
        WHERE L.id_red=l_id_red
        AND L.largo_acumulado>=l_distancia_aux;
        
        /*Se verifica si existe una linea anterior*/
        SELECT COUNT(*) INTO l_cantidad_aux
        FROM redes_lineas
        WHERE id_red=l_id_red
        AND id_linea<l_id_linea;
        
        IF l_cantidad_aux>0 THEN
            SELECT l_distancia_aux-MAX(largo_acumulado) INTO l_largo_acumulado
            FROM redes_lineas
            WHERE id_red=l_id_red
            AND id_linea<l_id_linea;
        ELSE
            /*Se trata de la primera linea*/
            SET l_largo_acumulado=l_distancia_aux;
        END IF;
        
        /*Se halla el segmento sobre el que se encuentra el punto generado y la distancia del punto inicial al inicio de la línea*/
        SELECT MAX(T.num_punto), MAX(largo_acumulado) INTO l_num_punto_ini, l_largo_acumulado_ini
        FROM (
        SELECT LD.num_punto, (
            SELECT SUM(LDD.largo_segmento)
            FROM redes_lineas_det LDD
            WHERE LDD.id_red=LD.id_red
            AND LDD.id_linea=LD.id_linea
            AND LDD.num_punto<=LD.num_punto
        ) AS largo_acumulado
        FROM redes_lineas_det LD
        WHERE LD.id_red=l_id_red
        AND LD.id_linea=l_id_linea
        ORDER BY LD.num_punto
        ) T
        WHERE T.largo_acumulado<l_largo_acumulado;
        
        /*Se halla la distancia a la que se debe hallar el punto aleatorio con respecto al inicio de la línea*/
        SET l_largo_acumulado_act=l_largo_acumulado-l_largo_acumulado_ini;
        
        /*Se halla la distancia del punto final al inicio de la linea*/
        SELECT MIN(T.num_punto), MIN(largo_acumulado) INTO l_num_punto_fin, l_largo_acumulado_fin
        FROM (
        SELECT LD.num_punto, (
            SELECT SUM(LDD.largo_segmento)
            FROM redes_lineas_det LDD
            WHERE LDD.id_red=LD.id_red
            AND LDD.id_linea=LD.id_linea
            AND LDD.num_punto<=LD.num_punto
        ) AS largo_acumulado
        FROM redes_lineas_det LD
        WHERE LD.id_red=l_id_red
        AND LD.id_linea=l_id_linea
        ORDER BY LD.num_punto
        ) T
        WHERE T.largo_acumulado>=l_largo_acumulado;
        
        /*Se hallan las coordenadas del punto inicial*/
        SELECT latitud, longitud INTO l_latitud_ini, l_longitud_ini
        FROM redes_lineas_det
        WHERE id_red=l_id_red
        AND id_linea=l_id_linea
        AND num_punto=l_num_punto_ini;
        
        /*Se hallan las coordenadas del punto final*/
        SELECT latitud, longitud INTO l_latitud_fin, l_longitud_fin
        FROM redes_lineas_det
        WHERE id_red=l_id_red
        AND id_linea=l_id_linea
        AND num_punto=l_num_punto_fin;
        
        /*Se interpolan de forma lineal las coordenadas del punto aleatorio*/
        SET l_factor_aux=l_largo_acumulado_act/(l_largo_acumulado_fin-l_largo_acumulado_ini);
        SET l_latitud=l_latitud_ini+(l_latitud_fin-l_latitud_ini)*l_factor_aux;
        SET l_longitud=l_longitud_ini+(l_longitud_fin-l_longitud_ini)*l_factor_aux;
        
        /*Se agrega el punto generado a la red*/
        INSERT INTO tmp_eventos_puntos
        (id_evento, id_punto, fecha_punto, latitud, longitud, id_red, id_linea, num_punto)
        VALUES (l_id_evento_tmp, l_id_punto, NOW(), l_latitud, l_longitud, l_id_red, l_id_linea, l_num_punto_ini);
        
        SET l_id_punto=l_id_punto+1;
    END WHILE;
    
    COMMIT;
    
    SET out_id_evento=l_id_evento_tmp;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_realizar_proyeccion_evento` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_realizar_proyeccion_evento`(IN in_id_evento BIGINT(20),
                                                 IN in_id_evento_tmp BIGINT(20),
                                                 in in_dist_proy DOUBLE,
                                                 OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_red BIGINT(20);
    DECLARE l_factor_metros DOUBLE;
    DECLARE l_ind_grados INT(1);
    DECLARE l_id_punto BIGINT(20);
    DECLARE l_latitud DOUBLE;
    DECLARE l_longitud DOUBLE;
    DECLARE l_id_linea_proy BIGINT(20);
    DECLARE l_num_punto_proy BIGINT(20);
    DECLARE l_latitud_proy DOUBLE;
    DECLARE l_longitud_proy DOUBLE;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    
    SET out_resultado=-1;
    
    START TRANSACTION;
    
    /*Se borran todas las proyecciones anteriores del evento*/
    UPDATE eventos_puntos
    SET latitud_proy=NULL,
    longitud_proy=NULL,
    id_red=NULL,
    id_linea=NULL,
    num_punto=NULL
    WHERE id_evento=in_id_evento;
    
    /*Se actualizan los datos de proyección*/
    UPDATE eventos_puntos
    INNER JOIN tmp_eventos_puntos ON eventos_puntos.id_punto=tmp_eventos_puntos.id_punto
    SET eventos_puntos.latitud_proy=tmp_eventos_puntos.latitud,
    eventos_puntos.longitud_proy=tmp_eventos_puntos.longitud,
    eventos_puntos.id_red=tmp_eventos_puntos.id_red,
    eventos_puntos.id_linea=tmp_eventos_puntos.id_linea,
    eventos_puntos.num_punto=tmp_eventos_puntos.num_punto
    WHERE eventos_puntos.id_evento=in_id_evento
    AND tmp_eventos_puntos.id_evento=in_id_evento_tmp;
    
    /*Se marca el evento como proyectado*/
    UPDATE eventos
    SET ind_proy=1,
    dist_proy=in_dist_proy
    WHERE id_evento=in_id_evento;
    
    /*Se borran los datos temporales*/
    DELETE FROM tmp_eventos_puntos_atributos
    WHERE id_evento=in_id_evento_tmp;
    
    DELETE FROM tmp_eventos_puntos
    WHERE id_evento=in_id_evento_tmp;
    
    DELETE FROM tmp_eventos_atributos
    WHERE id_evento=in_id_evento_tmp;
    
    DELETE FROM tmp_eventos
    WHERE id_evento=in_id_evento_tmp;
    
    COMMIT;
    
    SET out_resultado=1;
END */$$
DELIMITER ;

/* Procedure structure for procedure `pa_realizar_proyeccion_evento_old` */

DELIMITER $$

/*!50003 CREATE PROCEDURE `pa_realizar_proyeccion_evento_old`(IN in_id_evento BIGINT(20),
                                                     IN in_dist_proy DOUBLE,
                                                     OUT out_resultado INT(1))
    MODIFIES SQL DATA
BEGIN
    DECLARE l_id_red BIGINT(20);
    DECLARE l_factor_metros DOUBLE;
    DECLARE l_ind_grados INT(1);
    DECLARE l_id_punto BIGINT(20);
    DECLARE l_latitud DOUBLE;
    DECLARE l_longitud DOUBLE;
    DECLARE l_id_linea_proy BIGINT(20);
    DECLARE l_num_punto_proy BIGINT(20);
    DECLARE l_latitud_proy DOUBLE;
    DECLARE l_longitud_proy DOUBLE;
    DECLARE l_ind_fin INT;
    
    DECLARE cur_puntos CURSOR FOR
        SELECT id_punto, latitud, longitud
        FROM eventos_puntos
        WHERE id_evento=in_id_evento
        ORDER BY id_punto;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;
    DECLARE EXIT HANDLER FOR SQLWARNING ROLLBACK;
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET l_ind_fin=1;
    
    SET out_resultado=-1;
    
    START TRANSACTION;
    
    /*Se borran todas las proyecciones anteriores del evento*/
    UPDATE eventos_puntos
    SET latitud_proy=NULL,
    longitud_proy=NULL,
    id_red=NULL,
    id_linea=NULL,
    num_punto=NULL
    WHERE id_evento=in_id_evento;
    
    /*Se busca el identificador de la red sobre la que se harán las proyecciones*/
    SELECT E.id_red, UM.factor_metros, UM.ind_grados INTO l_id_red, l_factor_metros, l_ind_grados
    FROM eventos E
    INNER JOIN redes R ON E.id_red=R.id_red
    INNER JOIN sistemas_coordenadas SC ON R.id_sistema=SC.id_sistema
    INNER JOIN unidades_medida UM ON SC.id_unidad=UM.id_unidad
    WHERE E.id_evento=in_id_evento;
    
    /*Se recorren los puntos y se calculan las distancias*/
    SET l_ind_fin=0;
    OPEN cur_puntos;
    
    REPEAT
        FETCH cur_puntos INTO l_id_punto, l_latitud, l_longitud;
        
        IF NOT l_ind_fin THEN
            /*Se busca el segmento de red más cercano al punto*/
            CALL pa_buscar_linea_det_cercana(l_id_red, in_dist_proy, l_latitud, l_longitud, l_factor_metros, l_ind_grados, @id_linea, @num_punto, @latitud, @longitud);
            
            SET l_id_linea_proy=@id_linea;
            SET l_num_punto_proy=@num_punto;
            SET l_latitud_proy=@latitud;
            SET l_longitud_proy=@longitud;
            
            /*Si se halló una proyección, se actualiza en el punto*/
            IF l_id_linea_proy IS NOT NULL THEN
                UPDATE eventos_puntos
                SET latitud_proy=l_latitud_proy,
                longitud_proy=l_longitud_proy,
                id_red=l_id_red,
                id_linea=l_id_linea_proy,
                num_punto=l_num_punto_proy
                WHERE id_evento=in_id_evento
                AND id_punto=l_id_punto;
            END IF;
        END IF;
    UNTIL l_ind_fin END REPEAT;
    
    CLOSE cur_puntos;
    
    /*Se marca el evento como proyectado*/
    UPDATE eventos
    SET ind_proy=1,
    dist_proy=in_dist_proy
    WHERE id_evento=in_id_evento;
    
    COMMIT;
    
    SET out_resultado=1;
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
