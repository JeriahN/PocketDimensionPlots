package net.coolsimulations.PocketDimensionPlots;

import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.minecraft.server.MinecraftServer;

public class PDPServerLang {

	public static String langTranslations(MinecraftServer server, String key) {

		if(server.isSingleplayer()) {
			return key;
		} else {
			if(PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("es_ar")
					|| PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("es_cl")
					|| PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("es_es")
					|| PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("es_mx")
					|| PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("es_uy")
					|| PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("es_ve")) {
				if(key.equals("pdp.name"))
					return "Parcelas de Dimensión de Bolsillo";
				else if(key.equals("pdp.commands.pdp.teleport_into_player_plot"))
					return "Teletransportándose a la parcela de %s...";
				else if(key.equals("pdp.commands.pdp.teleport_into_plot"))
					return "Teletransportándose a la parcela...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot"))
					return "Teletransportándose afuera...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_plot"))
					return "Teletransportándose afuera porque el dueño abandonó la parcela...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_game"))
					return "Teletransportándose afuera porque el dueña abandonó la partida...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_not_online"))
					return "Teletransportándose afuera porque el dueña no está en línea...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_kicked"))
					return "Teletransportándose afuera porque el dueño te expulsado...";
				else if(key.equals("pdp.commands.pdp.create_plot"))
					return "Haga clic en el tamaño de una isla de inicio para crear una parcela: %s %s";
				else if(key.equals("pdp.commands.pdp.create_plot.small"))
					return "[Pequeño]";
				else if(key.equals("pdp.commands.pdp.create_plot.large"))
					return "[Grande]";
				else if(key.equals("pdp.commands.pdp.create_plot.tooltip"))
					return "Una isla de bloques de %sx%sx%s hecha de %s";
				else if(key.equals("pdp.commands.pdp.set_safe"))
					return "Se ha establecido el punto de generación de la parcela";
				else if(key.equals("pdp.commands.pdp.set_safe.not_owner"))
					return "¡No puedes configurar el punto de generación de esta parcela!";
				else if(key.equals("pdp.commands.pdp.kick"))
					return "Se ha expulsado a %s de la parcela";
				else if(key.equals("pdp.commands.pdp.kick.admin"))
					return "¡No puedes expulsar a los administradores!";
				else if(key.equals("pdp.commands.pdp.kick.whitelist"))
					return "¡No puedes expulsar a los jugadores incluidos en la lista blanca en este parcela!";
				else if(key.equals("pdp.commands.pdp.kick.not_whitelisted"))
					return "¡%s no está en una parcela desde la que puedas expulsar!";
				else if(key.equals("pdp.commands.pdp.kick.not_in_plot"))
					return "¡No tienes permitido expulsar en este parcela!";
				else if(key.equals("pdp.commands.pdp.kick.no_plot"))
					return "¡No estás en una parcela desde la que puedas expulsar!";
				else if(key.equals("pdp.commands.pdp.enter.send"))
					return "Solicitado de teletransporte enviada";
				else if(key.equals("pdp.commands.pdp.enter.recieve"))
					return "¡%s acaba de solicitado ingresar a su parcela! Haga clic aquí o escriba /pdp accept %s para aceptar";
				else if(key.equals("pdp.commands.pdp.accept"))
					return "Solicitado aceptada";
				else if(key.equals("pdp.commands.pdp.accept.no_request"))
					return "¡%s no te ha enviado una solicitado!";
				else if(key.equals("pdp.commands.pdp.request_expired"))
					return "Solicitud caducada";
				else if(key.equals("pdp.commands.pdp.sameTarget.kick"))
					return "¡No puedes expulsar a ti mismo!";
				else if(key.equals("pdp.commands.pdp.sameTarget.whitelist"))
					return "¡No puedes incluirte en la lista blanca!";
				else if(key.equals("pdp.commands.pdp.sameTarget.enter"))
					return "¡Use /pdp para ingresar su parcela!";
				else if(key.equals("pdp.commands.pdp.sameTarget.accept"))
					return "¡No puedes aceptar tu propia solicitud!";
				else if(key.equals("pdp.commands.pdp.not_player"))
					return "¡Debes ser un jugador para hacer eso!";
				else if(key.equals("pdp.commands.pdp.has_island"))
					return "¡Ya has creado una parcela!";
				else if(key.equals("pdp.commands.pdp.no_plot"))
					return "¡Primero debes crear una parcela!";
				else if(key.equals("pdp.commands.pdp.other_no_plot"))
					return "¡%s no tiene una parcela!";
				else if(key.equals("pdp.commands.pdp.need_whitelist"))
					return "¡No está incluido en la lista blanca de este parcela!";
				else if(key.equals("pdp.commands.pdp.not_in_plot"))
					return "¡%s no está en una parcela!";
				else if(key.equals("pdp.commands.pdp.gobber_ring"))
					return "¡No puedes teletransportarte en el mundo de la parcela!";
				else if(key.equals("pdp.update.display1"))
					return "¡Esta es una versión antigua de %s! ¡La Versión %s ya está disponible!";
				else if(key.equals("pdp.update.display2"))
					return "¡Por favor haga clic para descargar!";
				else if(key.equals("pdp.update.display3"))
					return "¡%s ya no es compatible con Minecraft Versión %s! ¡Actualiza a una versión más nueva de Minecraft para obtener más funciones!";
			} else if (PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("jp_jp")) {
				if(key.equals("pdp.name"))
					return "ポケット次元プロット";
				else if(key.equals("pdp.commands.pdp.teleport_into_player_plot"))
					return "%s のプロットにテレポートしています...";
				else if(key.equals("pdp.commands.pdp.teleport_into_plot"))
					return "プロットにテレポートしています....";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot"))
					return "外にテレポート...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_plot"))
					return "所有者がプロットを離れたため、外にテレポートします...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_game"))
					return "所有者がゲームを離れたため、外部にテレポートします...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_not_online"))
					return "所有者がオンラインではないため、外部にテレポートします...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_kicked"))
					return "所有者があなたを蹴ったので、外にテレポートします...";
				else if(key.equals("pdp.commands.pdp.create_plot"))
					return "スポーン島のサイズをクリックしてプロットを作成します: %s %s";
				else if(key.equals("pdp.commands.pdp.create_plot.small"))
					return "[小さな]";
				else if(key.equals("pdp.commands.pdp.create_plot.large"))
					return "[大きい]";
				else if(key.equals("pdp.commands.pdp.create_plot.tooltip"))
					return "%sx%sx%s ブロック アイランド。 %s 製";
				else if(key.equals("pdp.commands.pdp.set_safe"))
					return "プロット スポーン セット";
				else if(key.equals("pdp.commands.pdp.set_safe.not_owner"))
					return "このプロットのスポーンを設定することはできません！";
				else if(key.equals("pdp.commands.pdp.kick"))
					return "%s はプロットから追い出されました";
				else if(key.equals("pdp.commands.pdp.kick.admin"))
					return "管理者を蹴ることはできません！";
				else if(key.equals("pdp.commands.pdp.kick.whitelist"))
					return "このプロットでホワイトリストに登録されたプレイヤーをキックすることはできません！";
				else if(key.equals("pdp.commands.pdp.kick.not_whitelisted"))
					return "この陰謀に足を踏み入れることは許されていません！";
				else if(key.equals("pdp.commands.pdp.kick.not_in_plot"))
					return "あなたはキックできるプロットにいません！";
				else if(key.equals("pdp.commands.pdp.kick.no_plot"))
					return "%s はキックできるプロットにありません！";
				else if(key.equals("pdp.commands.pdp.enter.send"))
					return "テレポート要求が送信されました";
				else if(key.equals("pdp.commands.pdp.enter.recieve"))
					return "%s がプロットへの参加をリクエストしました! ここをクリックするか、「/pdp accept %s」と入力して受け入れます";
				else if(key.equals("pdp.commands.pdp.accept"))
					return "リクエストが承認されました";
				else if(key.equals("pdp.commands.pdp.accept.no_request"))
					return "%s はあなたにリクエストを送信していません！";
				else if(key.equals("pdp.commands.pdp.request_expired"))
					return "リクエストの期限が切れました";
				else if(key.equals("pdp.commands.pdp.sameTarget.kick"))
					return "あなたは自分自身を蹴ることはできません！";
				else if(key.equals("pdp.commands.pdp.sameTarget.whitelist"))
					return "自分自身をホワイトリストに登録することはできません！";
				else if(key.equals("pdp.commands.pdp.sameTarget.enter"))
					return "「/pdp」 を使用してプロットを入力します！";
				else if(key.equals("pdp.commands.pdp.sameTarget.accept"))
					return "自分の要求を受け入れることはできません！";
				else if(key.equals("pdp.commands.pdp.not_player"))
					return "あなたはそれを行うプレイヤーでなければなりません！";
				else if(key.equals("pdp.commands.pdp.has_island"))
					return "すでにプロットを作成しています！";
				else if(key.equals("pdp.commands.pdp.no_plot"))
					return "最初にプロットを作成する必要があります！";
				else if(key.equals("pdp.commands.pdp.other_no_plot"))
					return "%s にはプロットがありません！";
				else if(key.equals("pdp.commands.pdp.need_whitelist"))
					return "あなたはこのプロットでホワイトリストに登録されていません！";
				else if(key.equals("pdp.commands.pdp.not_in_plot"))
					return "%s はプロットにありません！";
				else if(key.equals("pdp.commands.pdp.gobber_ring"))
					return "プロットの世界ではテレポートできません！";
				else if(key.equals("pdp.update.display1"))
					return "これは%sの古いバージョンです！ バージョン%sが利用可能になりました！";
				else if(key.equals("pdp.update.display2"))
					return "クリックしてダウンロードしてください！";
				else if(key.equals("pdp.update.display3"))
					return "%sはMinecraftバージョン%sをサポートしなくなりました！ より多くの機能については新しいMinecraftバージョンに更新してください！";
			} else if (PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("ru_ru")) {
				if(key.equals("pdp.name"))
					return "Графики карманного измерения";
				else if(key.equals("pdp.commands.pdp.teleport_into_player_plot"))
					return "Телепортация к сюжету %s...";
				else if(key.equals("pdp.commands.pdp.teleport_into_plot"))
					return "Телепортация на участок...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot"))
					return "Телепортация наружу...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_plot"))
					return "Телепортация наружу, потому что владелец покинул участок...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_game"))
					return "Телепортация наружу, потому что владелец вышел из игры...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_not_online"))
					return "Телепортация наружу, потому что владелец не в сети...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_kicked"))
					return "Телепортация на улицу, потому что хозяин пнул вас...";
				else if(key.equals("pdp.commands.pdp.create_plot"))
					return "Нажмите на размер острова возрождения, чтобы создать график: %s %s";
				else if(key.equals("pdp.commands.pdp.create_plot.small"))
					return "[Небольшой]";
				else if(key.equals("pdp.commands.pdp.create_plot.large"))
					return "[Большой]";
				else if(key.equals("pdp.commands.pdp.create_plot.tooltip"))
					return "Блок-остров %sx%sx%s из %s";
				else if(key.equals("pdp.commands.pdp.set_safe"))
					return "Набор спавнов сюжета";
				else if(key.equals("pdp.commands.pdp.set_safe.not_owner"))
					return "Вы не можете установить спавн этого сюжета!";
				else if(key.equals("pdp.commands.pdp.kick"))
					return "%s был исключен из сюжета";
				else if(key.equals("pdp.commands.pdp.kick.admin"))
					return "Нельзя пинать админов!";
				else if(key.equals("pdp.commands.pdp.kick.whitelist"))
					return "Вы не можете исключать игроков из белого списка в этом сюжете!";
				else if(key.equals("pdp.commands.pdp.kick.not_whitelisted"))
					return "Вы не можете пинать в этом заговоре!";
				else if(key.equals("pdp.commands.pdp.kick.not_in_plot"))
					return "Вы не в заговоре, из которого можно выгнать!";
				else if(key.equals("pdp.commands.pdp.kick.no_plot"))
					return "%s не участвует в сюжете, из которого можно выкинуть!";
				else if(key.equals("pdp.commands.pdp.enter.send"))
					return "Запрос на телепорт отправлен";
				else if(key.equals("pdp.commands.pdp.enter.recieve"))
					return "%s только что запросил доступ к вашему участку! Нажмите здесь или введите /pdp accept %s, чтобы принять";
				else if(key.equals("pdp.commands.pdp.accept"))
					return "Запрос принят";
				else if(key.equals("pdp.commands.pdp.accept.no_request"))
					return "%s не отправил вам запрос!";
				else if(key.equals("pdp.commands.pdp.request_expired"))
					return "Срок действия запроса истек";
				else if(key.equals("pdp.commands.pdp.sameTarget.kick"))
					return "Вы не можете ударить себя!";
				else if(key.equals("pdp.commands.pdp.sameTarget.whitelist"))
					return "Вы не можете внести себя в белый список!";
				else if(key.equals("pdp.commands.pdp.sameTarget.enter"))
					return "Используйте /pdp, чтобы войти в свой сюжет!";
				else if(key.equals("pdp.commands.pdp.sameTarget.accept"))
					return "Вы не можете принять собственный запрос!";
				else if(key.equals("pdp.commands.pdp.not_player"))
					return "Вы должны быть игроком, чтобы сделать это!";
				else if(key.equals("pdp.commands.pdp.has_island"))
					return "Вы уже создали сюжет!";
				else if(key.equals("pdp.commands.pdp.no_plot"))
					return "Вы должны сначала создать сюжет!";
				else if(key.equals("pdp.commands.pdp.other_no_plot"))
					return "%s не имеет сюжета!";
				else if(key.equals("pdp.commands.pdp.need_whitelist"))
					return "Вы не внесены в белый список на этом участке!";
				else if(key.equals("pdp.commands.pdp.not_in_plot"))
					return "%s не находится на графике!";
				else if(key.equals("pdp.commands.pdp.gobber_ring"))
					return "Вы не можете телепортироваться в сюжетный мир!";
				else if(key.equals("pdp.update.display1"))
					return "Это старая версия %s! Версия %s теперь доступно!";
				else if(key.equals("pdp.update.display2"))
					return "Пожалуйста нажмите чтобы скачать!";
				else if(key.equals("pdp.update.display3"))
					return "%s больше не поддерживает версию Minecraft %s! Пожалуйста обновите Minecraft до новой версии чтобы получить больше возможностей!";
			} else if (PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("zh_cn")) {
				if(key.equals("pdp.name"))
					return "袖珍维度图";
				else if(key.equals("pdp.commands.pdp.teleport_into_player_plot"))
					return "传送到 %s 的地块...";
				else if(key.equals("pdp.commands.pdp.teleport_into_plot"))
					return "传送到阴谋...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot"))
					return "传送到外面...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_plot"))
					return "传送到外面因为主人离开了地块...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_game"))
					return "由于所有者离开游戏而传送到外面...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_not_online"))
					return "传送到外面因为主人不在线...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_kicked"))
					return "因为主人踢你而传送到外面...";
				else if(key.equals("pdp.commands.pdp.create_plot"))
					return "单击生成岛大小以创建地块: %s %s";
				else if(key.equals("pdp.commands.pdp.create_plot.small"))
					return "[小的]";
				else if(key.equals("pdp.commands.pdp.create_plot.large"))
					return "[大]";
				else if(key.equals("pdp.commands.pdp.create_plot.tooltip"))
					return "一个 %sx%sx%s 方块岛。 由 %s 制成";
				else if(key.equals("pdp.commands.pdp.set_safe"))
					return "情节生成集";
				else if(key.equals("pdp.commands.pdp.set_safe.not_owner"))
					return "你不能设置这个地块的产卵！";
				else if(key.equals("pdp.commands.pdp.kick"))
					return "%%s 被踢出地块";
				else if(key.equals("pdp.commands.pdp.kick.admin"))
					return "你不能踢管理员！";
				else if(key.equals("pdp.commands.pdp.kick.whitelist"))
					return "你不能踢这个剧情中的白名单玩家！";
				else if(key.equals("pdp.commands.pdp.kick.not_whitelisted"))
					return "你不能参与这个阴谋！";
				else if(key.equals("pdp.commands.pdp.kick.not_in_plot"))
					return "你不在你可以踢的阴谋中！";
				else if(key.equals("pdp.commands.pdp.kick.no_plot"))
					return "%s 不在你可以踢的阴谋中！";
				else if(key.equals("pdp.commands.pdp.enter.send"))
					return "传送请求已发送";
				else if(key.equals("pdp.commands.pdp.enter.recieve"))
					return "%s 刚刚请求进入你的情节！ 单击此处或键入 /pdp accept %s 接受";
				else if(key.equals("pdp.commands.pdp.accept"))
					return "请求被接受";
				else if(key.equals("pdp.commands.pdp.accept.no_request"))
					return "没有向您发送请求！";
				else if(key.equals("pdp.commands.pdp.request_expired"))
					return "请求过期";
				else if(key.equals("pdp.commands.pdp.sameTarget.kick"))
					return "你不能踢自己！";
				else if(key.equals("pdp.commands.pdp.sameTarget.whitelist"))
					return "你不能将自己列入白名单！";
				else if(key.equals("pdp.commands.pdp.sameTarget.enter"))
					return "使用 /pdp 进入你的情节！";
				else if(key.equals("pdp.commands.pdp.sameTarget.accept"))
					return "你不能接受你自己的请求！";
				else if(key.equals("pdp.commands.pdp.not_player"))
					return "你必须是一名球员才能做到这一点！";
				else if(key.equals("pdp.commands.pdp.has_island"))
					return "你已经创建了一个情节！";
				else if(key.equals("pdp.commands.pdp.no_plot"))
					return "您必须先创建一个图！";
				else if(key.equals("pdp.commands.pdp.other_no_plot"))
					return "%s 没有情节！";
				else if(key.equals("pdp.commands.pdp.need_whitelist"))
					return "您未在此地块上列入白名单！";
				else if(key.equals("pdp.commands.pdp.not_in_plot"))
					return "%s 不在情节中！";
				else if(key.equals("pdp.commands.pdp.gobber_ring"))
					return "你不能在剧情世界中传送！";
				else if(key.equals("pdp.update.display1"))
					return "这是%s的旧版本！ 版本%s现已可用！";
				else if(key.equals("pdp.update.display2"))
					return "请点击下载！";
				else if(key.equals("pdp.update.display3"))
					return "%s不再支持Minecraft版本%s！ 请更新到较新的Minecraft版本以获取更多功能！";
			} else if (PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("zh_tw")) {
				if(key.equals("pdp.name"))
					return "袖珍維度圖";
				else if(key.equals("pdp.commands.pdp.teleport_into_player_plot"))
					return "傳送到 %s 的地塊...";
				else if(key.equals("pdp.commands.pdp.teleport_into_plot"))
					return "傳送到陰謀...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot"))
					return "傳送到外面...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_plot"))
					return "傳送到外面因為主人離開了地塊...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_game"))
					return "由于所有者離開遊戲而傳送到外面...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_not_online"))
					return "傳送到外面因為主人不在線...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_kicked"))
					return "因為主人踢你而傳送到外面...";
				else if(key.equals("pdp.commands.pdp.create_plot"))
					return "單擊生成島大小以創建地塊: %s %s";
				else if(key.equals("pdp.commands.pdp.create_plot.small"))
					return "[小的]";
				else if(key.equals("pdp.commands.pdp.create_plot.large"))
					return "[大]";
				else if(key.equals("pdp.commands.pdp.create_plot.tooltip"))
					return "一個 %sx%sx%s 方塊島。 由 %s 制成";
				else if(key.equals("pdp.commands.pdp.set_safe"))
					return "情節生成集";
				else if(key.equals("pdp.commands.pdp.set_safe.not_owner"))
					return "你不能設置這個地塊的産卵！";
				else if(key.equals("pdp.commands.pdp.kick"))
					return "%s 被踢出地塊";
				else if(key.equals("pdp.commands.pdp.kick.admin"))
					return "你不能踢管理員！";
				else if(key.equals("pdp.commands.pdp.kick.whitelist"))
					return "你不能踢這個劇情中的白名單玩家！";
				else if(key.equals("pdp.commands.pdp.kick.not_whitelisted"))
					return "你不能參與這個陰謀！";
				else if(key.equals("pdp.commands.pdp.kick.not_in_plot"))
					return "你不在你可以踢的陰謀中！";
				else if(key.equals("pdp.commands.pdp.kick.no_plot"))
					return "%s 不在你可以踢的陰謀中！";
				else if(key.equals("pdp.commands.pdp.enter.send"))
					return "傳送請求已發送";
				else if(key.equals("pdp.commands.pdp.enter.recieve"))
					return "%s 剛剛請求進入你的情節！ 單擊此處或鍵入 /pdp accept %s 接受";
				else if(key.equals("pdp.commands.pdp.accept"))
					return "請求被接受";
				else if(key.equals("pdp.commands.pdp.accept.no_request"))
					return "%s 沒有向您發送請求！";
				else if(key.equals("pdp.commands.pdp.request_expired"))
					return "請求過期";
				else if(key.equals("pdp.commands.pdp.sameTarget.kick"))
					return "你不能踢自己！";
				else if(key.equals("pdp.commands.pdp.sameTarget.whitelist"))
					return "你不能將自己列入白名單！";
				else if(key.equals("pdp.commands.pdp.sameTarget.enter"))
					return "使用 /pdp 進入你的情節！";
				else if(key.equals("pdp.commands.pdp.sameTarget.accept"))
					return "你不能接受你自己的請求！";
				else if(key.equals("pdp.commands.pdp.not_player"))
					return "你必須是一名球員才能做到這一點！";
				else if(key.equals("pdp.commands.pdp.has_island"))
					return "你已經創建了一個情節！";
				else if(key.equals("pdp.commands.pdp.no_plot"))
					return "您必須先創建一個圖！";
				else if(key.equals("pdp.commands.pdp.other_no_plot"))
					return "%s 沒有情節！";
				else if(key.equals("pdp.commands.pdp.need_whitelist"))
					return "您未在此地塊上列入白名單！";
				else if(key.equals("pdp.commands.pdp.not_in_plot"))
					return "%s 不在情節中！";
				else if(key.equals("pdp.commands.pdp.gobber_ring"))
					return "你不能在劇情世界中傳送！";
				else if(key.equals("pdp.update.display1"))
					return "這是%s的舊版本！ 版本%s現已可用！";
				else if(key.equals("pdp.update.display2"))
					return "請點擊下載！";
				else if(key.equals("pdp.update.display3"))
					return "%s不再支持Minecraft版本%s！ 請更新到較新的Minecraft版本以獲取更多功能！";
			} else {
				if(key.equals("pdp.name"))
					return PDPReference.MOD_NAME;
				else if(key.equals("pdp.commands.pdp.teleport_into_player_plot"))
					return "Teleporting to %s's plot...";
				else if(key.equals("pdp.commands.pdp.teleport_into_plot"))
					return "Teleporting to plot...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot"))
					return "Teleporting outside...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_plot"))
					return "Teleporting outside because the owner left the plot...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_game"))
					return "Teleporting outside because the owner left the game...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_not_online"))
					return "Teleporting outside because the owner is not online...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_kicked"))
					return "Teleporting outside because the owner kicked you...";
				else if(key.equals("pdp.commands.pdp.create_plot"))
					return "Click a Spawn Island Size to Create a Plot: %s %s";
				else if(key.equals("pdp.commands.pdp.create_plot.small"))
					return "[Small]";
				else if(key.equals("pdp.commands.pdp.create_plot.large"))
					return "[Large]";
				else if(key.equals("pdp.commands.pdp.create_plot.tooltip"))
					return "A %sx%sx%s block island made of %s";
				else if(key.equals("pdp.commands.pdp.set_safe"))
					return "Plot Spawn Set";
				else if(key.equals("pdp.commands.pdp.set_safe.not_owner"))
					return "You can't set the spawn of this plot!";
				else if(key.equals("pdp.commands.pdp.kick"))
					return "%s was kicked from the plot";
				else if(key.equals("pdp.commands.pdp.kick.admin"))
					return "You can't kick admins!";
				else if(key.equals("pdp.commands.pdp.kick.whitelist"))
					return "You can't kick whitelisted players in this plot!";
				else if(key.equals("pdp.commands.pdp.kick.not_whitelisted"))
					return "You are not allowed to kick in this plot!";
				else if(key.equals("pdp.commands.pdp.kick.not_in_plot"))
					return "You are not in a plot you can kick from!";
				else if(key.equals("pdp.commands.pdp.kick.no_plot"))
					return "%s is not in a plot you can kick from!";
				else if(key.equals("pdp.commands.pdp.enter.send"))
					return "Teleport request sent";
				else if(key.equals("pdp.commands.pdp.enter.recieve"))
					return "%s has just requested to enter your plot! Click here or type /pdp accept %s to accept";
				else if(key.equals("pdp.commands.pdp.accept"))
					return "Request accepted";
				else if(key.equals("pdp.commands.pdp.accept.no_request"))
					return "%s has not sent you a request!";
				else if(key.equals("pdp.commands.pdp.request_expired"))
					return "Request expired";
				else if(key.equals("pdp.commands.pdp.sameTarget.kick"))
					return "You can't kick yourself!";
				else if(key.equals("pdp.commands.pdp.sameTarget.whitelist"))
					return "You can't whitelist yourself!";
				else if(key.equals("pdp.commands.pdp.sameTarget.enter"))
					return "Use /pdp to enter your plot!";
				else if(key.equals("pdp.commands.pdp.sameTarget.accept"))
					return "You can't accept your own request!";
				else if(key.equals("pdp.commands.pdp.not_player"))
					return "You must be a player to do that!";
				else if(key.equals("pdp.commands.pdp.has_island"))
					return "You have already created a plot!";
				else if(key.equals("pdp.commands.pdp.no_plot"))
					return "You must first create a plot!";
				else if(key.equals("pdp.commands.pdp.other_no_plot"))
					return "%s doesn't have a plot!";
				else if(key.equals("pdp.commands.pdp.need_whitelist"))
					return "You are not whitelisted on this plot!";
				else if(key.equals("pdp.commands.pdp.not_in_plot"))
					return "%s is not in a plot!";
				else if(key.equals("pdp.commands.pdp.gobber_ring"))
					return "You can't teleport in the plot world!";
				else if(key.equals("pdp.update.display1"))
					return "This is an old version of %s! Version %s is now available!";
				else if(key.equals("pdp.update.display2"))
					return "Please click to download!";
				else if(key.equals("pdp.update.display3"))
					return "%s no longer supports Minecraft Version %s! Please update to a newer Minecraft Version for more features!";
			}
			
			return "pdp.missing.translation";
		}
	}
}
