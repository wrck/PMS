<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div :id="tabContentId" class="tab-conent">
	<ul :id="navTabWrapper" v-if="navTabList.length > 0" class="nav nav-tabs">
		<li v-for="navTab in navTabList"><a :href="'#' + navTab.type + 'Tab'" data-toggle="tab" class="tab-bg-primary" aria-expanded="true">{{navTab.title}}</a></li>
	</ul>
	<div class="tab-pane fade" v-for="navTab in navTabList" :id="navTab.type + 'Tab'" :data-config="JSON.stringify(navTab)">
		<div class="box box-primary mb-0">
			<div class="box-body">
				<div class="overlay"><i class="fa fa-refresh fa-spin"></i></div>
				<div :id="navTab.type + 'SearchDiv'" v-if="navTab.operations.length > 0">
					<div class="btn-group operate-btn-group">
                         <button type="button" class="btn btn-default" v-for="btn in navTab.operations" :data-btn-type="btn.id" v-on:click="btn.events['click']">{{btn.text}}</button>
                     </div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	var tabVueConfig = {
		el: "#app",
		data: {
			tabContentId: "",
			navTabWrapper: "",
			navTabList: [{
				url: "list.json",
				type: "task",
				title: "任务列表",
				operations:[{
					id: 'add',
					text: '新增',
					events: {
						click: function(e) {
							console.log(e);
						}
					}
				}]
			}]
		}
	};
</script>