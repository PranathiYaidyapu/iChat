package com.ichat.ichat.pojo;

import java.util.List;

public record ContentModerationResponse(String safety, List<String> categories,String raw){}
